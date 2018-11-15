package com.cmatch.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmatch.domain.ChatRoom;
import com.cmatch.domain.User;
import com.cmatch.dto.CommonMessage;
import com.cmatch.dto.OutputMessage;
import com.cmatch.service.ChatService;
import com.cmatch.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ChatController {

    private final UserService userService;
    private final ChatService chatRoomService;

    public ChatController(UserService userService, ChatService chatRoomService) {
        this.userService = userService;
        this.chatRoomService = chatRoomService;
    }

    @MessageMapping("/chat/message/{room}")
    @SendTo("/topic/{room}")
    public OutputMessage sendChatMessage(Principal principal, CommonMessage message,
            @DestinationVariable("room") String roomName) {

        String userEmail = principal.getName();

        chatRoomService.saveChatMessage(message.getText(), userEmail, roomName);

        User user = userService.getUser(userEmail);

        log.info("Publishing message to message broker. Message from user {} : {}", principal.getName(), message);

        return new OutputMessage(message.getText(), userEmail, user.getImage());
    }

    @GetMapping("/chatRooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoom>> getUserChatRooms(Principal principal) {

        log.info("user {} requested list of chat rooms.", principal.getName());

        List<ChatRoom> chatRoomList = chatRoomService.getChatRooms(principal.getName());

        return new ResponseEntity<>(chatRoomList, HttpStatus.OK);
    }
}
