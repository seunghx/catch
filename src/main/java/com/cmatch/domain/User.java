package com.cmatch.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.cmatch.security.UserRole;
import com.cmatch.support.code.Gender;
import com.cmatch.support.code.Grade;
import com.cmatch.support.code.LoveStyle;
import com.cmatch.support.code.Major;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(exclude = "password")
public class User implements Serializable {

    private static final long serialVersionUID = 8910986008339413032L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;
    @JsonIgnore
    private String password;
    private String name;
    private int age;

    private String phone;
    private String image;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Major major;
    @Enumerated(EnumType.STRING)
    private Grade grade;
    @Enumerated(EnumType.STRING)
    private LoveStyle loveStyle;

}
