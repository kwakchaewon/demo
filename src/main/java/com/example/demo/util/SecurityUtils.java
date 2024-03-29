package com.example.demo.util;

import com.example.demo.dto.response.BoardDto;
import com.example.demo.entity.Board;
import org.springframework.security.core.Authentication;

public class SecurityUtils {
    private String[] superOrAdmin = {"ROLE_SUPERVISOR", "ROLE_ADMIN"};

    public static boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    public static boolean isSupervisor(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_SUPERVISOR".equals(authority.getAuthority()));
    }

    public static boolean isAdminOrSuper(Authentication authentication) {
        return isAdmin(authentication) || isSupervisor(authentication);
    }

    public static boolean isWriter(Authentication authentication, String memberId) {
        String _userId = authentication.getName();
//        String memberId = boardDto.getMemberId();

        if (_userId.equals(memberId)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getAuthority(Authentication authentication) {
        String auth = authentication.getAuthorities().stream().findFirst().get().getAuthority();
        return auth;
    }
}
