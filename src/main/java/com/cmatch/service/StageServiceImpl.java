package com.cmatch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import com.cmatch.domain.User;
import com.cmatch.dto.MatchingCriteria;
import com.cmatch.dto.MatchingRequest;
import com.cmatch.dto.Notification;
import com.cmatch.dto.MatchingResponse.CommonMatchingResponse;
import com.cmatch.dto.MatchingResponse.MatchingAcceptResponse;
import com.cmatch.persistence.UserRepository;
import com.cmatch.stage.InstantChatRoom;
import com.cmatch.stage.ScoreCalculator;
import com.cmatch.support.FollowingEstablishedEvent;
import com.cmatch.support.code.MatchingMessageType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static com.cmatch.dto.Notification.LeaveNotification;

@Slf4j
@Service
public class StageServiceImpl implements StageService, ApplicationEventPublisherAware {
    
    private static final String FOLLOWING_FAIL_MESSAGE = "%s님과의 팔로잉에 실패하였습니다.";
    private static final String FOLLOWING_SUCCESS_MESSAGE = "%s님과의 팔로잉에 성공하였습니다.";
    private static final String INSTANT_PARTNER_LEAVE_MESSAGE = "상대방이 stage를 떠났습니다.";
    
    private static final int INSTANT_CHAT_TIMER_THREAD_CNT = 3;

    private static final Map<String, MatchingCandidate> users = new HashMap<>();
    private static final Map<String, InstantChatRoom> chatRooms = new HashMap<>();

    // Injected fields

    private final UserRepository userRepo;
    private final SimpMessagingTemplate msgTemplate;
    private final ScoreCalculator scoreCalculator;
    private final ScheduledExecutorService instantChatExecutorService;
    
    private ApplicationEventPublisher eventPublisher;

    @Value("${followingChoice.timeInterval.sec}")
    private long followingChoiceIntervalSec;

    /**
     * instant chat 종료 후 following 결정하기 까지에 대한 시간 여유분.
     * 
     * instant chat이 종료되었음을 알리는 요청 이후 {@code instantChatTimeoverSpareSec} 시간 내에
     * following 결정 요청이 전달되지 않을 경우(네트워크 문제, 사용자 서비스 종료 등의 이유로.) following 실패처리를 하게
     * 된다.
     * 
     * 프론트엔드에서 매칭이 성립 되어 두 사용자가 instant chat을 수행할 때 서버로 instant chat이 시작되었음을 알리고
     * 서버에서 {@link java.util.Timer}나
     * {@link java.util.concurrent.ScheduledExecutorService} 등을 이용하여 시간이 지나면 프론트엔드로
     * instant chat 제한 시간이 지났음을 알리는 방법도 있으나 이럴 경우 사용자가 실제로 instant chat을 이용하는 시간과
     * 서버가 instant chat 종료를 위해 지정한 시간을 제대로 맞추지 못하면 사용자에게 불만이 발생할 가능성이 있어 instant
     * chat에 대한 타이머 설정은 프론트 엔드에서 수행하게 하였다.
     * 
     */
    @Value("${instantChat.timeoverSpare.sec}")
    private long instantChatTimeoverSpareSec;


    public StageServiceImpl(UserRepository userRepo, SimpMessagingTemplate msgTemplate
                          , ScoreCalculator scoreCalculator) {
        this.userRepo = userRepo;
        this.msgTemplate = msgTemplate;
        this.scoreCalculator = scoreCalculator;

        instantChatExecutorService = Executors.newScheduledThreadPool(INSTANT_CHAT_TIMER_THREAD_CNT);
    }

    @Override
    public void addNewUserToStage(String email, String subscriptionId) {
        User user = userRepo.findByEmail(email);
        user.setPassword(null);

        MatchingCandidate candidate = new MatchingCandidate(user, subscriptionId);
        // candidate.getBlackList().add(email);

        users.put(email, candidate);
    }

    @Override
    public void deleteUserFromStage(String email) {
        if(StringUtils.isEmpty(email)) {
            log.error("Empty user email detected. "
                                    + "Checking security or preceding object codes required.");
        }
        users.remove(email);
        
        chatRooms.entrySet()
                 .parallelStream()
                 .map(entry -> entry.getValue())
                 .filter(room -> room.getRoomUsers().contains(email))
                 .forEach(room -> chatRooms.remove(room.getRoomId()));
    }

    @Override
    public void addUserToBlackList(String userEmail, String blockedUser) {
        users.get(userEmail).getBlackList().add(blockedUser);
    }

    /**
     * stage상에 존재않는 user의 요청인지 확인하기.
     */
    @Override
    public List<User> matching(String userEmail, MatchingCriteria criteria) {

        log.info("Matching started for user : {}", userEmail);

        validateStageUser(userEmail);

        return users.entrySet()
                    .parallelStream()
                    .map(entry -> entry.getValue())
                    .filter(candidate -> 
                        candidate.getUser().getGender() == criteria.getGender().getGender())
                //  .filter(candidate -> {
                //      return !candidate.getBlackList().contains(userEmail);
                //  })
                    .map(candidate -> {
                        ScoredUser scoredCandidate = new ScoredUser(candidate.getUser());
                        scoredCandidate.setScore(
                                    scoreCalculator.calculate(scoredCandidate.getUser(), criteria));
                        return scoredCandidate;
                    })
                    .sorted((candidate1, candidate2) -> {
                        if (candidate1.getScore() < candidate2.getScore()) {
                            return 1;
                        } else if (candidate1.getScore() > candidate2.getScore()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    })
                    .limit(criteria.getLimit())
                    .map(candidate -> {
                        return candidate.getUser();
                    })
                    .collect(Collectors.toList());
    }

    @Override
    public User getStageUser(String userEmail) {

        validateStageUser(userEmail);

        return users.get(userEmail).getUser();
    }

    @Override
    public void handleMatchingMessage(String userEmail, MatchingRequest matchingRequest) {

        log.info("Received matching request : {}", matchingRequest.getMessageType());

        if (!users.containsKey(matchingRequest.getTo())) {
           log.debug("Unknown sender detected. Maybe sender just leaves stage.");
           return;
        }

        if (matchingRequest.getMessageType() == MatchingMessageType.REQUEST) {

            CommonMatchingResponse response = new CommonMatchingResponse(MatchingMessageType.REQUEST
                                                                       , users.get(userEmail).getUser());

            msgTemplate.convertAndSendToUser(matchingRequest.getTo(), "/queue/stage", response);

        } else if (matchingRequest.getMessageType() == MatchingMessageType.ACCEPT) {

            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            
            msgTemplate.convertAndSendToUser(matchingRequest.getTo(), "/queue/stage"
                   , new MatchingAcceptResponse(MatchingMessageType.ACCEPT, userEmail, uuid));

            msgTemplate.convertAndSendToUser(userEmail, "/queue/stage"
                   , new MatchingAcceptResponse(MatchingMessageType.ACCEPT, matchingRequest.getTo(), uuid));

            InstantChatRoom chatRoom = new InstantChatRoom(uuid, userEmail, matchingRequest.getTo());

            chatRooms.put(uuid, chatRoom);

        } else if (matchingRequest.getMessageType() == MatchingMessageType.DENY) {

            // users.get(userEmail).getBlackList().add(matchingRequest.getTo());

            CommonMatchingResponse response = new CommonMatchingResponse(MatchingMessageType.DENY,
                    users.get(userEmail).getUser());

            msgTemplate.convertAndSendToUser(matchingRequest.getTo(), "/queue/stage", response);
        }
    }

    @Override
    public void instantChatTimeover(String userEmail, String roomId) {
        validateStageUser(userEmail);
        
        setFollowingChoiceTimeover(roomId);
    }

    @Override
    public void choiceFollowing(String userEmail, String roomId, boolean follow) {
        validateStageUser(userEmail);
        
        chatRooms.get(roomId).setfollowingChoice(userEmail, follow);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent e) {
       handleStageLeaveEvent(e);
    }
    
    @EventListener
    public void onUnsubscribeEvent(SessionUnsubscribeEvent e) {
        handleStageLeaveEvent(e);
    }
    
    private void handleStageLeaveEvent(AbstractSubProtocolEvent e) {
        if(e.getUser() == null) {
            log.error("Null value user principal detected.");
            log.error("Cheching security or preceding object codes, required.");
            throw new IllegalStateException("User principal is null.");
        }        
        
        String userName = e.getUser().getName();
        
        if(isUserExist(userName)) {
            sendLeaveMessageToChatPartners(userName);
            deleteUserFromStage(e.getUser().getName());
        }
        
    }
    
    private void validateStageUser(String userEmail) {
        if(!isUserExist(userEmail)) {
            log.error("Illegal request detected. requested user is not on stage.");
            throw new IllegalStateException();
            // 400 error 관련 예외로 변경.
        }
    }

    /**
     * 현재 stage 상에 존재하는 유저인지 검사.
     * 
     */
    private boolean isUserExist(String userEmail) {
       return Optional.ofNullable(users.get(userEmail))
                      .filter(stageUser -> stageUser.getUser() != null)
                      .isPresent();
    }
    
    private void sendLeaveMessageToChatPartners(String userName) {
        chatRooms.entrySet().parallelStream()
                            .map(room -> room.getValue())
                            .filter(room -> room.getRoomUsers().contains(userName))
                            .forEach(room -> {
                                msgTemplate
                                    .convertAndSend("/stage/chat/" + room.getRoomId()
                                                  , new LeaveNotification(INSTANT_PARTNER_LEAVE_MESSAGE));
                            });
    }

    private void setFollowingChoiceTimeover(String roomId) {
        instantChatExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                handleFollowing(roomId);
            }
        }, followingChoiceIntervalSec + instantChatTimeoverSpareSec, TimeUnit.SECONDS);
    }

    /**
     * follow 관계가 성립되면 이를 의미하는 {@link FollowingEstablishedEvent}를 발생시킨다.
     * 현재로서는 {@link ChatServiceImpl} 클래스로만 전달되는데 이 특정 클래스의 메서드를 호출하는 방법이 아닌,
     * Evnet publishing 방법을 선택한 이유는 첫 째, 팔로우 관계가 성립될 때 추가적인 기능이 필요하게 될 경우에 
     * 변화가 적다는 장점이 있으며 다음으로, 특정 클래스의 메서드를 호출하는 방법은 그만큼 의존성이 증가한다는 뜻이므로 
     * 클래스 간의(특히 타 도메인 서비스 클래스 간의) 의존성을 완화시키기 위함이다.
     */
    private void handleFollowing(String roomId) {

        if (chatRooms.get(roomId).isFollowingEstablished()) {
            String[] chatRoomUsers = chatRooms.get(roomId).getRoomUsers().toArray(new String[] {});

            msgTemplate.convertAndSendToUser(chatRoomUsers[0], "/queue/stage",
                    new Notification(String.format(FOLLOWING_SUCCESS_MESSAGE, chatRoomUsers[1])));

            msgTemplate.convertAndSendToUser(chatRoomUsers[1], "/queue/stage",
                    new Notification(String.format(FOLLOWING_SUCCESS_MESSAGE, chatRoomUsers[0])));

            User user1 = users.get(chatRoomUsers[0]).getUser();
            User user2 = users.get(chatRoomUsers[1]).getUser();

            FollowingEstablishedEvent event = new FollowingEstablishedEvent(this, user1, user2);
            eventPublisher.publishEvent(event);

        } else {
            String[] chatRoomUsers = chatRooms.get(roomId).getRoomUsers().toArray(new String[] {});

            users.get(chatRoomUsers[0]).getBlackList().add(chatRoomUsers[1]);
            users.get(chatRoomUsers[1]).getBlackList().add(chatRoomUsers[0]);

            msgTemplate.convertAndSendToUser(chatRoomUsers[0], "/queue/stage",
                    new Notification(String.format(FOLLOWING_FAIL_MESSAGE, chatRoomUsers[1])));

            msgTemplate.convertAndSendToUser(chatRoomUsers[1], "/queue/stage",
                    new Notification(String.format(FOLLOWING_FAIL_MESSAGE, chatRoomUsers[0])));
        }

        chatRooms.remove(roomId);
    }

    @Getter
    @ToString
    private class MatchingCandidate {
        private final User user;
        private final String subscriptionId;
        private final List<String> blackList = new ArrayList<>();

        private MatchingCandidate(User user, String subscriptionId) {
            this.user = user;
            this.subscriptionId = subscriptionId;
        }
    }

    private class ScoredUser {
        @Getter
        private final User user;
        @Setter
        @Getter
        private int score;

        private ScoredUser(User user) {
            this.user = user;
        }
    }

}
