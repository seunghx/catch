package com.cmatch.service;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.cmatch.domain.ChatMessage;
import com.cmatch.domain.ChatRoom;
import com.cmatch.domain.User;
import com.cmatch.persistence.ChatMessageRepository;
import com.cmatch.persistence.ChatRoomRepository;
import com.cmatch.persistence.UserRepository;
import com.cmatch.support.FollowingEstablishedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatServiceImpl(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository,
            UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveChatMessage(String message, String sender, String roomName) {
        validateRoom(roomName);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setSender(userRepository.findByEmail(sender));
        chatMessage.setRoom(chatRoomRepository.FindByName(roomName));

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

    @Override
    public List<ChatRoom> getChatRooms(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        return chatRoomRepository.findByRoomUser(user);
    }

    @EventListener
    public void onApplicationEvent(FollowingEstablishedEvent event) {
        List<User> users = event.getUsers();

        createChatRoom(UUID.randomUUID().toString().replaceAll("-", ""), users.get(0), users.get(1));
    }

    private void validateRoom(String roomName) {
        if (chatRoomRepository.FindByName(roomName) == null) {
            throw new IllegalArgumentException();
        }
    }

}
