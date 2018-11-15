package com.cmatch.support.code;

public enum MatchingMessageType implements KeyValueShapedCode {

    REQUEST, ACCEPT, DENY, EXEPTION;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getCode() {
        return name();
    }

}
