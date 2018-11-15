package com.cmatch.stage;

import org.springframework.stereotype.Component;

import com.cmatch.domain.User;
import com.cmatch.dto.MatchingCriteria;

@Component
public class DefaultScoreCalculator implements ScoreCalculator {

    @Override
    public int calculate(User user, MatchingCriteria criteria) {
        int score = 1;

        if (user.getGrade() == criteria.getGrade().getGrade()) {
            score *= criteria.getGrade().getWeight();
        }

        if (user.getLoveStyle() == criteria.getLoveStyle().getLoveStyle()) {
            score *= criteria.getLoveStyle().getWeight();
        }

        if (user.getMajor() == criteria.getMajor().getMajor()) {
            score *= criteria.getMajor().getWeight();
        }

        return score;
    }

}
