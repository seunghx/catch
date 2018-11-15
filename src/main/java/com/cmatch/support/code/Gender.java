package com.cmatch.support.code;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Gender implements Code {
    MALE("남"), FEMALE("여");

    private final String code;

    Gender(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getCode() {
        return code;
    }

}
