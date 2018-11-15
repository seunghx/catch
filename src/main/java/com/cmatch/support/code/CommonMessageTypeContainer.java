package com.cmatch.support.code;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = KeyValueShapedSerializer.class)
public class CommonMessageTypeContainer implements KeyValueShapedCodeContainer {
    
    private static List<CommonMessageType> messageTypes = Arrays.asList(CommonMessageType.values());

    public List<CommonMessageType> getKeyValueShapedCode() {
        return Collections.unmodifiableList(messageTypes);
    }
}
