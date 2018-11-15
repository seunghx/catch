package com.cmatch.stage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InstantChatTimeLimit {
    private long chatTimeLimitInterval;
    private long followingChoiceTimeLimitInterval;
}
