package com.cmatch.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * matching 및 instant chatting을 거쳐 최종 팔로우(실제로는 팔로우라기보다는 친구 등록)를 거칠 경우 자동으로 채팅방에
 * subscription하게 된다. 아래 클래스 {@link ChatRoom} 이런 경우에 사용된다.
 * 
 * 
 * @author leeseunghyun
 *
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "room")
    private List<ChatMessage> messages = new ArrayList<>();
    @OneToOne
    @JoinColumn(name = "user1_id")
    private User user1;
    @OneToOne
    @JoinColumn(name = "user2_id")
    private User user2;
}
