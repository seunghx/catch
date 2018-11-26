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

import lombok.extern.slf4j.Slf4j;


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
                                        , (dest, sender) -> 
                                                       dest.setFrom(sender));
                       
                       mapper.<String>map(source -> source.getSender().getImage()
                                        , (dest, senderImage) -> 
                                                       dest.setFromImage(senderImage));
                   });
    }

    // Methods
    // ==========================================================================================================================

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

    @Override
    public List<ChatRoom> getUserChatRooms(String userEmail) {
        
        return Optional.ofNullable(userRepository.findByEmail(userEmail))
                       .map(user -> chatRoomRepository.findByRoomUser(user))
                       .orElseThrow(() -> {
                          log.error("Authenticated username {} does not exist in DB.");
                          throw new IllegalStateException("Authenticated username does not exist in DB.");   
                       });
    }

    @Override
    public List<ChatOutputMessage> getChatMessage(String userName, ChatMessageCriteria criteria) {
        
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

    @EventListener
    public void onApplicationEvent(FollowingEstablishedEvent event) {
        List<User> users = event.getUsers();

        createChatRoom(UUID.randomUUID().toString().replaceAll("-", ""), users.get(0), users.get(1));
    }

    private ChatRoom validateRoom(String roomName) {
       return Optional.ofNullable(chatRoomRepository.FindByName(roomName))
                      .orElseThrow(() -> {
                          log.error("Received roomName : {} does not exist.");
                          throw new IllegalArgumentException("Non-existed roomName detected. roomName : " 
                                                           + roomName);
                      });
    }    
}
