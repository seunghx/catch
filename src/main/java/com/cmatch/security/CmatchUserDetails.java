package com.cmatch.security;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.cmatch.domain.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CmatchUserDetails implements UserDetails {

    private static final long serialVersionUID = -79596970997479964L;

    private final User user;

    public CmatchUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> userRoles = 
                Optional.ofNullable(user.getRole().roles())
                        .orElseThrow(() -> {
                            log.info("Illegal state detected. User role information from database is null.");
                            log.warn("Database record for user {}  might be changed illegally.", user.getId());
                            
                            throw new InternalAuthenticationServiceException("Illegal state detected."
                                                                           + "User role is null.");
        });

        return AuthorityUtils.createAuthorityList(userRoles.toArray(new String[userRoles.size()]));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
