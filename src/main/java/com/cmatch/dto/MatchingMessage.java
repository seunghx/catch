package com.cmatch.dto;

import com.cmatch.support.code.MatchingMessageType;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MatchingMessage extends CommonMessage {

    private String to;
    private MatchingMessageType messageType;

    public MatchingMessage(String text, String to, MatchingMessageType messageType) {
        super(text);
        this.to = to;
        this.messageType = messageType;
    }

}
