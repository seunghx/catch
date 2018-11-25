package com.cmatch.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString(callSuper=true)
@NoArgsConstructor
public class ChatOutputMessage extends OutputMessage {
    
    private Integer id;
    
    @JsonFormat
    (shape = JsonFormat.Shape.STRING, pattern = "hh:mm:ss")
    private LocalDateTime writeTime;
    
    public ChatOutputMessage(String text, String from, String fromImage, Integer id, LocalDateTime writeTime) {
        super(text, from, fromImage);
        this.id = id;
        this.writeTime = writeTime;
    }
}
