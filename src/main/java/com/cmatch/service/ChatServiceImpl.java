package com.cmatch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cmatch.domain.ChatMessage;
import com.cmatch.domain.ChatRoom;
import com.cmatch.domain.User;
import com.cmatch.dto.ChatMessageCriteria;
import com.cmatch.dto.ChatOutputMessage;
import com.cmatch.persistence.ChatMessageRepository;
import com.cmatch.persistence.ChatRoomRepository;
import com.cmatch.persistence.UserRepository;
import com.cmatch.support.FollowingEstablishedEvent;
import com.cmatch.support.NoSuchRoomException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Follow 관계에 있는 유저 끼리의 채팅 관련 로직을 수행한다.
 * 
 * 아직은 채팅 외에 특별한 기능이 없기 때문에 Follow 자체를 ChatRoom으로 취급하였다.
 * 후에 기능이 추가될 경우 이 클래스가 구현하는 {@link ChatService}외에 
 * FollowService와 같은 인터페이스를 생성하게 된다면 이 클래스의 기능이 분리될 것이다.
 * 
 * @author leeseunghyun
 *
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {
    
    // Static Fields
    // ==========================================================================================================================

    private static final int DEFAULT_START_PAGE = 0;
    
    // Instance Fields
    // ==========================================================================================================================

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    // Constructors 
    // ==========================================================================================================================

    public ChatServiceImpl(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository
                         , UserRepository userRepository) {
        
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        
        this.modelMapper = new ModelMapper();
        
        modelMapper.createTypeMap(ChatMessage.class, ChatOutputMessage.class)
                   .addMappings(mapper -> { 
                       
                       mapper.<String>map(source -> source.getSender().getEmail()
                                        , (dest, sender) -> dest.setFrom(sender));
                       
                       mapper.<String>map(source -> source.getSender().getImage()
                                        , (dest, senderImage) -> dest.setFromImage(senderImage));
                   });
    }

    // Methods
    // ==========================================================================================================================

    /**
     * 채팅방에서 전송된 메세지를 저장한다. 현재는 RDB에 저장을 하는데, 성능을 고려하면 redis와 같은 in-memory db에 저장 후 
     * 나중에 일괄적으로 RDB에 저장하는 방법도 나쁘지 않을 것 같다. 
     */
    @Override
    public void saveChatMessage(String text, String sender, String roomName) {
        
        ChatRoom chatRoom = validateRoom(roomName);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setText(text);
        chatMessage.setSender(userRepository.findByEmail(sender));
        chatMessage.setRoom(chatRoom);

        chatMessageRepository.saveAndFlush(chatMessage);
    }

    @Override
    public void createChatRoom(String roomName, User user1, User user2) {

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(roomName);
        chatRoom.setUser1(user1);
        chatRoom.setUser2(user2);

        chatRoomRepository.saveAndFlush(chatRoom);
    }
    
    /**
     * 특정 유저가 속한 chat room(Follom) 목록을 반환.
     */
    @Override
    public List<ChatRoom> getUserChatRooms(String userEmail) {
        
        return Optional.ofNullable(userRepository.findByEmail(userEmail))
                       .map(user -> chatRoomRepository.findByRoomUser(user))
                       .orElseThrow(() -> {
                          log.error("Authenticated username {} does not exist in DB.");
                          throw new IllegalStateException("Authenticated username does not exist in DB.");   
                       });
    }

    /**
     * 
     * {@code criteria}를 바탕으로 채팅 룸의 메세지 목록을 반환한다.
     * 
     * {@code criteria.getOldestMessageId()} 메세지 호출의 결과가 null이 아닐 경우 이는 
     * 채팅룸 상의 스크롤 up을 통한 보다 이전 메세지 목록 요청으로부터 이 메서드가 호출된 경우이다.
     * 
     * 반대로 null인 경우는 채팅룸에 처음 입장할 때의 메세지 로딩을 위한 요청으로부터 이 메서드가 
     * 호출된 경우이다.
     * 
     */
    @Override
    public List<ChatOutputMessage> getChatMessage(ChatMessageCriteria criteria) {
        
       ChatRoom chatRoom = validateRoom(criteria.getRoomName());
       
       List<ChatMessage> chatMessages =  null;
       
       if(criteria.getOldestMessageId() == null) {
           chatMessages = chatMessageRepository
                               .findByRoom(chatRoom
                                         , PageRequest.of(DEFAULT_START_PAGE
                                                        , criteria.getMsgCntPerPage()
                                                        , Sort.by(Sort.Direction.DESC, "writeTime")));
       }else {
           chatMessages = chatMessageRepository
                         .findByRoomAndComparingTime(criteria.getOldestMessageId()
                                                   , chatRoom
                                                   , PageRequest.of(DEFAULT_START_PAGE
                                                                  , criteria.getMsgCntPerPage()
                                                                  , Sort.by(Sort.Direction.DESC, "writeTime")));
           
       }
       
       List<ChatOutputMessage> outputMessages = new ArrayList<>();
       chatMessages.stream().forEach(msg -> outputMessages.add(modelMapper.map(msg, ChatOutputMessage.class)));
       
       return outputMessages;
    }

    /**
     * 
     * Instant Chat을 진행한 두 유저의 선택을 바탕으로 Following 관계가 성립되면(현재로는 채팅룸이 하나만들어지는 것.)
     * 아래 event listener 메서드가 호출될 것이다.
     * 
     */
    @EventListener
    public void onApplicationEvent(FollowingEstablishedEvent event) {
        List<User> users = event.getUsers();

        createChatRoom(UUID.randomUUID().toString().replaceAll("-", ""), users.get(0), users.get(1));
    }

    private ChatRoom validateRoom(String roomName) {
       return Optional.ofNullable(chatRoomRepository.FindByName(roomName))
                      .orElseThrow(() -> {
                          log.error("Received roomName : {} does not exist.");
                          
                          throw new NoSuchRoomException("Non-existed roomName detected. "
                                                           + "roomName : " + roomName);
                      });
    }    
}
