package com.cmatch.support.code;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Getter;

@Getter
@Component
@JsonRootName("CodeBook")
public class CodeBook {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    List<Gender> gender = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    List<Grade> grade = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    List<LoveStyle> loveStyle = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    List<Major> major = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private CommonMessageTypeContainer commonMessageTypes;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private MatchingMessageTypeContainer matchingMessageTypes;
    
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private SubscriptionModeContainer subscriptionModes;

    public CodeBook() {
        for (Gender e : Gender.values()) {
            gender.add(e);
        }

        for (Grade e : Grade.values()) {
            grade.add(e);
        }

        for (LoveStyle e : LoveStyle.values()) {
            loveStyle.add(e);
        }

        for (Major e : Major.values()) {
            major.add(e);
        }

        this.commonMessageTypes = new CommonMessageTypeContainer();
        this.matchingMessageTypes = new MatchingMessageTypeContainer();
        this.subscriptionModes = new SubscriptionModeContainer();
    }

}