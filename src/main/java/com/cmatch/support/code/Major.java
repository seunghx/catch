package com.cmatch.support.code;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Major implements Code {

    COMPUTER_INFORMATION("컴퓨터정보공학과"), INFORMATION_COMMUNICATION_ELECTRONIC("정보통신전자공학과"), MEDIA_INFORMATION(
            "미디어정보공학과"), HUMANITY("인문학과"), MUSIC("음악학과"), LAW("법학과"), BUSINESS_ADMINISTRATION("경영학과"), SOCIAL_SCIENCE(
                    "사회과학과"), INTERNATIONAL("국제학과"), CLOTHING_TEXTILES(
                            "의류학과"), CONSUMER_AND_HOUSING("소비자주거학과"), CHILD_AND_FAMILLY("아동학과");

    private final String code;

    Major(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getCode() {
        return code;
    }

}
