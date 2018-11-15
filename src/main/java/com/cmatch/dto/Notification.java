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
}
