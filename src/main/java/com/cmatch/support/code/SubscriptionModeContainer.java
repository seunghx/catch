package com.cmatch.support.code;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize(using = KeyValueShapedSerializer.class)
public class SubscriptionModeContainer  implements KeyValueShapedCodeContainer {

    private final List<SubscriptionMode> subscriptionModes = Arrays.asList(SubscriptionMode.values());
    
    @Override
    public List<SubscriptionMode> getKeyValueShapedCode() {
        return subscriptionModes;
    }
}
