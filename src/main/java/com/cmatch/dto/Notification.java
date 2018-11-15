package com.cmatch.dto;

import static com.cmatch.support.code.CommonMessageType.NOTICE;

import com.cmatch.support.code.CommonMessageType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification {

    private String text;

    public Notification(String text) {
        this.text = text;
    }

    public CommonMessageType getMessageType() {
        return NOTICE;
    }
    
    /**
     * 
     * instant chat 중 상대방이 stage를 나가거나 브라우저를 종료할 경우 
     * chat 상대방에게 전달할 {@link Notification}
     * 
     * (전체적으로 Message DTO 수정을 해야겠다.)
     * 
     * @author leeseunghyun
     *
     */
    public static class LeaveNotification extends Notification{

        public LeaveNotification(String text) {
            super(text);
        }
        
        @Override
        public CommonMessageType getMessageType() {
            return CommonMessageType.PARTNER_LEAVE;
        }
    }
}
