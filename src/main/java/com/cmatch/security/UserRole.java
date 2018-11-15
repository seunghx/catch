package com.cmatch.security;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author leeseunghyun
 *
 */
public enum UserRole {

    ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

    private String[] roles;

    private UserRole(String... roles) {
        this.roles = roles;
    }

    public List<String> roles() {
        return Arrays.asList(roles);
    }
}
