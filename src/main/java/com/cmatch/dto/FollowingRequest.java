package com.cmatch.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FollowingRequest {
    private String roomId;
    private boolean follow;
}
