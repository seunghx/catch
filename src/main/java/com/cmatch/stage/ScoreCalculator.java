package com.cmatch.stage;

import com.cmatch.domain.User;
import com.cmatch.dto.MatchingCriteria;

public interface ScoreCalculator {
    public int calculate(User user, MatchingCriteria criteria);
}
