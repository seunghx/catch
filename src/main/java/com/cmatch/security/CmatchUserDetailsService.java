package com.cmatch.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cmatch.domain.User;
import com.cmatch.persistence.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CmatchUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CmatchUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public CmatchUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = Optional.ofNullable(userRepo.findByEmail(email)).orElseThrow(() -> {
            throw new UsernameNotFoundException("User email does not exist");
        });

        log.debug("User information successfully loaded from database. Loaded user info : {}.", user);

        return new CmatchUserDetails(user);
    }

}
