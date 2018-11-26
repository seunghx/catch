package com.cmatch.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmatch.domain.ChatRoom;
import com.cmatch.domain.User;
import com.cmatch.dto.ChatMessageCriteria;
import com.cmatch.dto.ChatOutputMessage;
import com.cmatch.dto.CommonMessage;
import com.cmatch.dto.OutputMessage;
import com.cmatch.service.ChatService;
import com.cmatch.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ChatController {
    
    // Instance Fields
    // ==========================================================================================================================

    private final UserService userService;
    private final ChatService chatService;

    // Constructors
    // ==========================================================================================================================

    public ChatController(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    // Methods
    // ==========================================================================================================================

    @MessageMapping("/chat/message/{room}")
    @SendTo("/topic/{room}")
    public OutputMessage sendChatMessage(Principal principal
                                       , CommonMessage message
                                       , @DestinationVariable("room") String roomName) {

        String userEmail = principal.getName();
        
        log.info("Saving message. Message from user {} {} {}.", message, message.getText(),  principal.getName());
        
        chatService.saveChatMessage(message.getText(), userEmail, roomName);

        User user = userService.getUser(userEmail);

        log.info("Publishing message to message broker. Message from user {} : {}", principal.getName(), message);

        return new OutputMessage(message.getText(), userEmail, user.getImage());
    }

    @GetMapping("/chatRooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoom>> getChatRooms(Principal principal) {

        log.info("User {} requested list of chat rooms.", principal.getName());

        List<ChatRoom> chatRoomList = chatService.getUserChatRooms(principal.getName());

        return new ResponseEntity<>(chatRoomList, HttpStatus.OK);
    }
    
    @GetMapping("/chatMessages")
    @ResponseBody
    public ResponseEntity<List<ChatOutputMessage>> getChatMessages(Principal principal
                                                      , @Validated ChatMessageCriteria criteria){
        
        if(log.isInfoEnabled()) {
            log.info("Received message request for chat room : {} by user : {}.", principal.getName()
                                                                                , criteria.getRoomName());
        }
        
        List<ChatOutputMessage> messages = chatService.getChatMessage(principal.getName(), criteria);
        log.error("{}",messages.size());
        log.info("Loaded messages : {}.", messages);
        
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}
