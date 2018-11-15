package com.cmatch.service;

import java.util.List;

import com.cmatch.domain.ChatRoom;
import com.cmatch.domain.User;

public interface ChatService {

    public List<ChatRoom> getChatRooms(String userEmail);

    public void createChatRoom(String roomId, User user1, User user2);

    public void saveChatMessage(String text, String from, String roomId);
}
