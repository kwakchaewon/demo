package com.example.demo.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.service.MemberService;
import com.example.demo.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class TokenRequestFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl userDetailsService;
    private final JWTUtil jwtUtil;

    public TokenRequestFilter(UserDetailsServiceImpl userDetailsService, JWTUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if ("/member/login".equals(request.getRequestURI()) || "/member/signup".equals(request.getRequestURI())) {
                doFilter(request, response, filterChain);
            } else {

                // 1. Access / Refresh 헤더에서 토큰을 가져옴.
                String accessToken = parseJwt(request, "ACCESS_TOKEN");
                String refreshToken = parseJwt(request, "ACCESS_TOKEN");

                if (accessToken != null) {
                    // 2-1. 어세스 토큰값이 유효하다면 setAuthentication를 통해 security context에 인증 정보저장
                    DecodedJWT tokenInfo = jwtUtil.decodeToken(accessToken);

                    if (tokenInfo != null) {
                        String userId = tokenInfo.getClaim("userId").asString();
                        UserDetails loginUser = userDetailsService.loadUserByUsername(userId);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                loginUser, null, loginUser.getAuthorities()
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        doFilter(request, response, filterChain);
                    }

                    // 2-2. 어세스 토큰이 만료된 상황 && 리프레시 토큰 또한 존재하는 상황
                    else if (refreshToken != null) {
                        // 리프레시 토큰 검증 && 리프레시 토큰 DB에서  토큰 존재유무 확인
                        boolean isRefreshToken = jwtUtil.validateRefreshToken(refreshToken);
                        // 리프레시 토큰이 유효하고 리프레시 토큰이 DB와 비교했을때 똑같다면
                        if (isRefreshToken) {
                            // 리프레시 토큰으로 아이디 정보 가져오기
                            String userId = jwtUtil.getUserIdFromToken(refreshToken);
                            // 새로운 어세스 토큰 발급
                            String newAccessToken = jwtUtil.createToken(userId);
                            // 헤더에 어세스 토큰 추가
                            jwtUtil.setHeaderAccessToken(response, newAccessToken);
                            // Security context에 인증 정보 넣기
                            UserDetails loginUser = userDetailsService.loadUserByUsername(userId);
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    loginUser, null, loginUser.getAuthorities()
                            );

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                        // 리프레시 토큰이 만료 || 리프레시 토큰이 DB와 비교했을때 똑같지 않다면
                        else {
                            log.error("### TokenInfo is Null");
                        }
                    }


                    // 기존 액세스 코드 관련 주석
//                String token = parseJwt(request);
//                if (token == null) {
//                    response.sendError(403);    //accessDenied
//                } else {
//                    DecodedJWT tokenInfo = jwtUtil.decodeToken(token);
//                    if (tokenInfo != null) {
//                        String userId = tokenInfo.getClaim("userId").asString();
//                        UserDetails loginUser = memberService.loadUserByUsername(userId);
//                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                                loginUser, null, loginUser.getAuthorities()
//                        );
//
//                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                        SecurityContextHolder.getContext().setAuthentication(authentication);
//                        doFilter(request, response, filterChain);
//
//                    } else {
//                        log.error("### TokenInfo is Null");
//                    }
//                }
                }
            }

        } catch (Exception e) {
            log.error("### Filter Exception {}", e.getMessage());
        }
    }

    private String parseJwt(HttpServletRequest request, String headerName) {
        String headerAuth = request.getHeader(headerName);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }
        return null;
    }
}
