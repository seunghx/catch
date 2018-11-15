package com.cmatch.support.code;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = KeyValueShapedSerializer.class)
public class MatchingMessageTypeContainer implements KeyValueShapedCodeContainer {

    private static List<MatchingMessageType> messageTypes = Arrays.asList(MatchingMessageType.values());

    public List<MatchingMessageType> getKeyValueShapedCode() {
        return Collections.unmodifiableList(messageTypes);
    }
}
