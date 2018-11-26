package com.cmatch.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.cmatch.dto.UserSignupDTO;
import com.cmatch.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserController {

    // Instance Fields
    // ==========================================================================================================================

    private final UserService userService;

    // Constructors
    // ==========================================================================================================================

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Methods
    // ==========================================================================================================================

    @GetMapping(value = "/user/login", produces = MediaType.TEXT_HTML_VALUE)
    public String helloAdmin() {

        log.debug("Passing request to login view.");

        return "login";
    }

    @PostMapping("/user/signup")
    public String signup(@Validated UserSignupDTO signupDTO) {

        userService.signup(signupDTO);
        return "success";
    }
}
