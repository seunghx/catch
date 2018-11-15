package com.cmatch.dto;

import com.cmatch.support.code.Gender;
import com.cmatch.support.code.Grade;
import com.cmatch.support.code.LoveStyle;
import com.cmatch.support.code.Major;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class MatchingCriteria {

    private GenderCriteria gender = new GenderCriteria();
    private GradeCriteria grade = new GradeCriteria();
    private LoveStyleCriteria loveStyle = new LoveStyleCriteria();
    private MajorCriteria major = new MajorCriteria();

    @Setter
    private int limit;

    @Getter
    @Setter
    @ToString
    public static class GenderCriteria {
        private Gender gender;
    }

    @Getter
    @Setter
    @ToString
    public static class GradeCriteria {
        private Grade grade;
        private int weight;

    }

    @Getter
    @Setter
    @ToString
    public static class LoveStyleCriteria {
        private LoveStyle loveStyle;
        private int weight;

    }

    @Getter
    @Setter
    @ToString
    public static class MajorCriteria {
        private Major major;
        private int weight;
    }
}
