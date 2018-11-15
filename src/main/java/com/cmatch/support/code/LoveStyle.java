package com.cmatch.support.code;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LoveStyle implements Code {
    DAY_WIN_NIGHT_WIN("낮이밤이"), DAY_WIN_NIGHT_LOSE("낮이밤져"), DAY_LOSE_NIGHT_WIN("낮져밤이"), DAY_LOSE_NIGHT_LOSE("낮져밤져");

    private final String code;

    LoveStyle(String code) {
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
