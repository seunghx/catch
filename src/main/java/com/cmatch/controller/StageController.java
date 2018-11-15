package com.cmatch.controller;

import java.security.Principal;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cmatch.domain.User;
import com.cmatch.dto.CommonMessage;
import com.cmatch.dto.FollowingRequest;
import com.cmatch.dto.MatchingCriteria;
import com.cmatch.dto.MatchingRequest;
import com.cmatch.dto.Notification;
import com.cmatch.dto.OutputMessage;
import com.cmatch.service.StageService;
import com.cmatch.stage.StageChatRoomInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class StageController {

    private static final String ENTRANCE_MSG = "%s 님이 입장하셨습니다.";

    private final StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }

    @SubscribeMapping("/stage")
    @SendTo("/stage")
    public Notification stageSubscription(Principal principal, StompHeaderAccessor accessor) {
         
        String userEmail = Optional.ofNullable(principal)
                                   .map(prcp -> prcp.getName())
                                   .orElseThrow(() -> {
                                       log.warn("Anonymous user request reached here. "
                                              + "It might be securiy config problem.");

                                       throw new IllegalStateException("Anonymous user request "
                                                                     + "reached controller.");
                                   });
        
        if(StringUtils.isEmpty(accessor.getSubscriptionId())) {
            log.error("Empty subscription id detected. Finding cause required.");
            throw new IllegalStateException("Empty subscription id detected.");
        }

        log.info("User subscribing to stage registerred. Subscribing user : {}", userEmail);

        stageService.addNewUserToStage(userEmail, accessor.getSubscriptionId());

        String entranceMessage = String.format(ENTRANCE_MSG, userEmail);

        return new Notification(entranceMessage);
    }

    @SubscribeMapping("/stage/chat/{room}")
    public void stageSubscription(Principal principal, @DestinationVariable("room") String roomId) {

        String userEmail = Optional.ofNullable(principal).map(prcp -> prcp.getName()).orElseThrow(() -> {
            log.warn("Anonymous user request reached here. It might be securiy config problem.");

            throw new IllegalStateException("Anonymous user request reached controller.");
        });

        log.info("User subscribed to instant chat room, id : {}. Subscribing user : {}", roomId, userEmail);

    }

    @MessageMapping("/stage/chat/{room}")
    @SendTo("/stage/chat/{room}")
    public OutputMessage sendStageChatMessage(Principal principal, CommonMessage message,
            @DestinationVariable("room") String roomId) {

        log.info("Publishing message to message broker. Message from user {} : {}", principal.getName(), message);

        User user = stageService.getStageUser(principal.getName());

        return new OutputMessage(message.getText(), principal.getName(), user.getImage());
    }

    @MessageMapping("/stage/message")
    @SendTo("/stage")
    public OutputMessage sendStageMessage(Principal principal, CommonMessage message) {

        log.info("Publishing message to message broker. Message from user {} : {}", principal.getName(), message);

        return new OutputMessage(message.getText(), principal.getName());
    }

    @GetMapping("/stage/matching")
    public ResponseEntity<List<User>> matching(Principal principal,
            @ModelAttribute("matchingCriteria") MatchingCriteria matchingCriteria) {

        log.info("Received maatching request from user : {}", principal.getName());

        List<User> matchedUsers = stageService.matching(principal.getName(), matchingCriteria);

        return new ResponseEntity<>(matchedUsers, HttpStatus.OK);
    }

    @PostMapping("/stage/matching")
    public ResponseEntity<Void> handleMatchingMessage(Principal principal,
            @RequestBody MatchingRequest matchingRequest) {

        stageService.handleMatchingMessage(principal.getName(), matchingRequest);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/stage/chat/timeout")
    public ResponseEntity<Void> instantChatTimeover(Principal principal, @RequestBody StageChatRoomInfo roomInfo) {

        stageService.instantChatTimeover(principal.getName(), roomInfo.getRoomId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/stage/chat/following")
    public ResponseEntity<Void> followingChoice(Principal principal, @RequestBody FollowingRequest request) {

        stageService.choiceFollowing(principal.getName(), request.getRoomId(), request.isFollow());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}