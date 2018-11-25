package com.cmatch.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageCriteria {
    
    @NotBlank
    private String roomName;
    @Positive
    private int msgCntPerPage;
    private Integer oldestMessageId;
}
