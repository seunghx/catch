package com.cmatch.support.code;

public enum CommonMessageType implements KeyValueShapedCode {
    NOTICE, ECHO, PARTNER_LEAVE;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getCode() {
        return name();
    }
}
