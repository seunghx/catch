package com.cmatch.dto;

import static com.cmatch.support.code.CommonMessageType.ECHO;

import com.cmatch.support.code.CommonMessageType;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OutputMessage extends CommonMessage {

    private String from;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fromImage;

    public OutputMessage(String text, String from) {
        super(text);
        this.from = from;
    }

    public OutputMessage(String text, String from, String fromImage) {
        super(text);
        this.from = from;
        this.fromImage = fromImage;
    }

    public CommonMessageType getMessageType() {
        return ECHO;
    }
}
