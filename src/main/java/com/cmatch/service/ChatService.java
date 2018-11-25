package com.cmatch.service;

import java.util.List;

import com.cmatch.domain.ChatRoom;
import com.cmatch.domain.User;
import com.cmatch.dto.ChatMessageCriteria;
import com.cmatch.dto.ChatOutputMessage;

public interface ChatService {

    public List<ChatRoom> getUserChatRooms(String userEmail);
    public List<ChatOutputMessage> getChatMessage(String userEmail, ChatMessageCriteria criteria);

    public void createChatRoom(String roomId, User user1, User user2);

    public void saveChatMessage(String text, String from, String roomId);
}
