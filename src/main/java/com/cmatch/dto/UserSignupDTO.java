package com.cmatch.dto;

import javax.validation.constraints.Max;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import com.cmatch.support.ImageExtension;
import com.cmatch.support.code.Gender;
import com.cmatch.support.code.Grade;
import com.cmatch.support.code.LoveStyle;
import com.cmatch.support.code.Major;

import lombok.Data;

/**
 * 
 * 회원 가입 요청에 사용될 form data 용 DTO 클래스이다.
 * 
 * @author leeseunghyun
 *
 */
@Data
public class UserSignupDTO {

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
    @NotBlank(message = "핸드폰 번호를 입력해주세요.")
    private String phone;

    @Min(value = 20, message = "미성년자는 이용 불가능합니다.")
    @Max(value = 29, message = "30세 미만의 회원만 이용 가능합니다.")
    private int age;

    @ImageExtension(message = "지원 가능한 포맷의 이미지 파일이 아닙니다.")
    private MultipartFile image;

    private Gender gender;
    private Grade grade;
    private Major major;
    private LoveStyle loveStyle;
}
