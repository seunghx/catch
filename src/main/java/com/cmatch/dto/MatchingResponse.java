package com.cmatch.dto;

import com.cmatch.domain.User;
import com.cmatch.support.code.MatchingMessageType;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class MatchingResponse {
    private MatchingMessageType messageType;

    public MatchingResponse(MatchingMessageType messageType) {
        this.messageType = messageType;
    }

    @Getter
    @ToString
    public static class CommonMatchingResponse extends MatchingResponse {
        private User from;

        public CommonMatchingResponse(MatchingMessageType messageType, User from) {
            super(messageType);
            this.from = from;
        }
    }

    @Getter
    @ToString
    public static class MatchingAcceptResponse extends MatchingResponse {
        private String partnerId;
        private String roomId;

        public MatchingAcceptResponse(MatchingMessageType messageType, String partnerId, String roomId) {
            super(messageType);
            this.partnerId = partnerId;
            this.roomId = roomId;
        }
    }

}
