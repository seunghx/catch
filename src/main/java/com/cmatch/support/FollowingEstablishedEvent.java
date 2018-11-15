package com.cmatch.support;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.cmatch.domain.User;

public class FollowingEstablishedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -5463983236272229417L;

    private final User user1;
    private final User user2;

    public FollowingEstablishedEvent(Object source, User user1, User user2) {
        super(source);
        this.user1 = user1;
        this.user2 = user2;
    }

    public List<User> getUsers() {
        return Arrays.asList(user1, user2);
    }
}
