package com.example.demo.util;

import com.example.demo.entity.Board;
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

    public static boolean isWriter(Authentication authentication, Board board){
        String _userId = authentication.getName();
        String board_userId = board.getMember().getUserId();

        if (_userId.equals(board_userId)){
            return true;
        }else {
            return false;
        }
    }

    public static String getAuthority(Authentication authentication){
        String auth = authentication.getAuthorities().stream().findFirst().get().getAuthority();
        return auth;
    }


}
