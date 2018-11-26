package com.cmatch.service;

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

        User user = Optional.ofNullable(signupDTO)
                            .map(dto -> modelMapper.map(dto, User.class))
                            .orElseThrow(() -> 
                                new IllegalArgumentException("Null Argument signupDTO detected."));
                
        userRepository.saveAndFlush(user);
    }

    @Override
    public User getUser(String userEmail) {
        return Optional.ofNullable(userRepository.findByEmail(userEmail))
                       .orElseThrow(() -> new IllegalArgumentException());
    }

}
