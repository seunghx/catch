package com.cmatch.dto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

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
    
    @Valid
    private GenderCriteria gender = new GenderCriteria();
    @Valid
    private GradeCriteria grade = new GradeCriteria();
    @Valid
    private LoveStyleCriteria loveStyle = new LoveStyleCriteria();
    @Valid
    private MajorCriteria major = new MajorCriteria();
    
    @Positive
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
        @Positive
        private int weight;

    }

    @Getter
    @Setter
    @ToString
    public static class LoveStyleCriteria {
        private LoveStyle loveStyle;
        @Positive
        private int weight;
    }

    @Getter
    @Setter
    @ToString
    public static class MajorCriteria {
        private Major major;
        @Positive
        private int weight;
    }
}
