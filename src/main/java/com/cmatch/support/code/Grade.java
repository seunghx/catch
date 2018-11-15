package com.cmatch.support.code;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Grade implements Code {

    FRESHMAN("1학년"), SOPHOMORE("2학년"), JUNIOR("3학년"), SENIOR("4학년");

    private final String code;

    Grade(String code) {
        this.code = code;
        ;
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
