package com.cmatch.stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Getter
@Slf4j
public class InstantChatRoom {

    // Static Fields
    // ==========================================================================================================================

    private static final int INSTANT_CHATROOM_CAPACITY = 2;

    // Instance Fields
    // ==========================================================================================================================

    private final Map<String, Boolean> followingChoices = new HashMap<>(INSTANT_CHATROOM_CAPACITY);
    private String roomId;
    private boolean timeover;

    // Constructors
    // ==========================================================================================================================

    public InstantChatRoom(String roomId, String user1, String user2) {
        this.roomId = roomId;
        followingChoices.put(user1, false);
        followingChoices.put(user2, false);
    }

    // Methods
    // ==========================================================================================================================

    public Set<String> getRoomUsers() {
        return followingChoices.keySet();
    }

    public void setfollowingChoice(String userEmail, boolean choiced) {
        validateUser(userEmail);

        if (!timeover) {
            followingChoices.put(userEmail, choiced);
        }
    }

    private void validateUser(String userEmail) {
        if (!followingChoices.keySet().contains(userEmail)) {
            log.warn("Illegal access detected. User {} is not member of this room {}.", userEmail, roomId);
            throw new IllegalArgumentException("User is not member of this room.");
        }
    }
    
    public void setTimeover() {
        if (!timeover) {
            timeover = true;
        }
    }

    public boolean isFollowingEstablished() {

        for (Entry<String, Boolean> entry : followingChoices.entrySet()) {
            if (!entry.getValue().booleanValue()) {
                return false;
            }
        }
        return true;
    }
}
