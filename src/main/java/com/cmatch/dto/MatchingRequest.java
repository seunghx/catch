package com.cmatch.dto;

import com.cmatch.support.code.MatchingMessageType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MatchingRequest {
    private String to;
    private MatchingMessageType messageType;
}
