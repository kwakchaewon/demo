package com.example.demo.util;

import org.springframework.security.core.Authentication;

public class SecurityUtils {

    public static boolean isAdmin(Authentication authentication){
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    public static boolean isSupervisor(Authentication authentication){
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_SUPERVISOR".equals(authority.getAuthority()));
    }
}
