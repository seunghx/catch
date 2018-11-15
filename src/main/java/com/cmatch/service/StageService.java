package com.cmatch.service;

import java.util.List;

import com.cmatch.domain.User;
import com.cmatch.dto.MatchingCriteria;
import com.cmatch.dto.MatchingRequest;

public interface StageService {

    public void addNewUserToStage(String userEmail, String subscriptionId);

    public void addUserToBlackList(String userEmail, String blockedUser);

    public void deleteUserFromStage(String userEmail);
    
    public User getStageUser(String userEmail);
    
    public List<User> matching(String userEmail, MatchingCriteria criteria);

    public void handleMatchingMessage(String userEmail, MatchingRequest matchingRequest);

    public void instantChatTimeover(String userEmail, String roomId);

    public void choiceFollowing(String userEmail, String roomId, boolean follow);
    
}
