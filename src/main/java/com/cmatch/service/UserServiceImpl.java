package com.cmatch.service;

import java.util.Objects;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cmatch.domain.User;
import com.cmatch.dto.UserSignupDTO;
import com.cmatch.persistence.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    
    // Instance Fields
    // ==========================================================================================================================

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    // Constructors
    // ==========================================================================================================================

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
    }
    
    // Methods
    // ==========================================================================================================================

    @Override
    public void signup(UserSignupDTO signupDTO) {
        Objects.requireNonNull(signupDTO, "Null Argument signupDTO detected.");

        User user = modelMapper.map(signupDTO, User.class);

        userRepository.save(user);
    }

    @Override
    public User getUser(String userEmail) {
        return Optional.ofNullable(userRepository.findByEmail(userEmail))
                .orElseThrow(() -> new IllegalArgumentException());
    }

}
