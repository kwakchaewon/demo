package com.example.demo.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
public class TokenRequestFilter extends OncePerRequestFilter {
    @Value("${jwt.secret_access}")
    private String secret_access;

    @Value("${jwt.secret_refresh}")
    private String secret_refresh;

    private final UserDetailsServiceImpl userDetailsService;
    private final JWTUtil jwtUtil;

    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    /**
     * Spring security 관련 보안 작업 수행
     * 모든 HTTP 요청에 대해 실행
     * /login, /signup 은 보안 작업 x
     * 나머지 작업에 대해서는 토큰 검증 및 보안 정보 설정, 토큰 발급
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("/member/login".equals(request.getRequestURI()) || "/member/signup".equals(request.getRequestURI())) {
            doFilter(request, response, filterChain);
        } else {
            // 1. Access / Refresh 헤더에서 토큰을 가져옴.
            String accessToken = parseJwt(request, ACCESS_TOKEN);
            String refreshToken = parseJwt(request, REFRESH_TOKEN);

            // 2-1. 어세스 토큰값이 유효하다면 setAuthentication를 통해 security context에 인증 정보저장
            if (accessToken != null && !accessToken.equals("undefined")) {
                DecodedJWT accessInfo = jwtUtil.decodeToken(accessToken, secret_access);

                if (accessInfo != null) {
                    String userId = accessInfo.getClaim("userId").asString();
                    UserDetails loginUser = userDetailsService.loadUserByUsername(userId);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            loginUser, null, loginUser.getAuthorities()
                    );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    doFilter(request, response, filterChain);
                }
            }

            // 2-2. 어세스 토큰이 유효하지 않는다면
            else if (refreshToken != null && !refreshToken.equals("undefined")) {
                boolean isRefreshToken = jwtUtil.validateRefreshToken(refreshToken);

                // 리프레시 토큰이 유효하다면 액세스 토큰 재발급
                if (isRefreshToken) {
                    DecodedJWT refreshInfo = jwtUtil.decodeToken(refreshToken, secret_refresh);
                    String userId = refreshInfo.getClaim("userId").asString();
                    String authority = refreshInfo.getClaim("authority").asString();

                    // 새로운 어세스 토큰 발급
                    String newAccessToken = jwtUtil.createToken(userId, authority, ACCESS_TOKEN);

                    // 헤더에 어세스 토큰 추가
                    response.setHeader(ACCESS_TOKEN, newAccessToken);
                    response.setHeader(REFRESH_TOKEN, refreshToken);

                    // Security context에 인증 정보 넣기
                    UserDetails loginUser = userDetailsService.loadUserByUsername(userId);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            loginUser, null, loginUser.getAuthorities()
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    doFilter(request, response, filterChain);
                }
            } else {
                log.error("### TokenInfo is unvalidated");
            }
        }
    }

    private String parseJwt(HttpServletRequest request, String headerName) {
        String headerAuth = request.getHeader(headerName);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
