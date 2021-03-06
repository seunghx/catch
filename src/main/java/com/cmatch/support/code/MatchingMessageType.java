package com.cmatch.support.code;

public enum MatchingMessageType implements KeyValueShapedCode {

    REQUEST, ACCEPT, DENY;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getCode() {
        return name();
    }

}
