package com.cmatch.support.code;

public enum SubscriptionMode implements KeyValueShapedCode {
    
    COMMON, STAGE;

    @Override
    public String getName() {
       return name();
    }

    @Override
    public String getCode() {
       return name();
    }

}
