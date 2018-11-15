package com.cmatch.service;

import com.cmatch.domain.User;
import com.cmatch.dto.UserSignupDTO;

public interface UserService {
    public void signup(UserSignupDTO signupDTO);

    public User getUser(String userEmail);
}
