package com.example.demo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.dto.response.TokenDto;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Optional;

/**
 * jwt 토큰 관련 유틸리티 클래스
 * 디코딩, 생성, 검증, REFRESH 메서드 정의
 * Refresh 토큰 검증시, 의존성이 주입된 repository 메서드를 호출하기때문에 @Component로 관리.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JWTUtil {
    private final MemberRepository memberRepository;
    @Value("${jwt.secret_access}")
    private String secret_access;

    @Value("${jwt.secret_refresh}")
    private String secret_refresh;
    public final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public final String REFRESH_TOKEN = "REFRESH_TOKEN";


    /**
     * 토큰 Decode
     */
    public DecodedJWT decodeToken(String token, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("vue-board")
                    .build();
            return verifier.verify(token);

        } catch (JWTVerificationException e) {
            log.error("JWTVerificationException: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return null;
    }


    // userId 기반 토큰 생성
    public String createToken(String userId, String authority, String type) {
        Date ACCESS_TIME = new Date(System.currentTimeMillis() + (3 * 60 * 60 * 1000L)); // 3시간
        Date REFRESH_TIME = new Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)); // 7일

        // 1. 토큰 타입에 따른 만료시간, 알고리즘 설정
        Date expTime = type.equals(ACCESS_TOKEN) ? ACCESS_TIME : REFRESH_TIME;
        Algorithm algorithm = type.equals(ACCESS_TOKEN) ? Algorithm.HMAC256(secret_access) : Algorithm.HMAC256(secret_refresh);

        // 2. userId, grantedAuth 정보 기반 토큰 생성
        return JWT.create()
                .withIssuer("vue-board")  // 발급자
                .withClaim("userId", userId) // 토큰 주체(userId)
                .withClaim("authority", authority)
                .withIssuedAt(new Date())  // 발급일자
                .withExpiresAt(expTime)  // 만료일자
                .sign(algorithm);
    }

    // 토큰 생성
    public TokenDto createTokenDto(String userId, String authority) {
        String access = createToken(userId, authority, ACCESS_TOKEN);
        String refresh = createToken(userId, authority, REFRESH_TOKEN);
        Date accessExpired = this.decodeToken(access, secret_access).getExpiresAt();
        Date refreshExpired = this.decodeToken(refresh, secret_refresh).getExpiresAt();

        return new TokenDto(access, refresh, accessExpired, refreshExpired, userId, authority);
    }

    //토큰 검증
    public Boolean validateToken(String token, String secret) {
        try {
            this.decodeToken(token, secret);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    // refreshToken 토큰 검증. db token과 비교
    public Boolean validateRefreshToken(String token) {

        // 1차 토큰 검증
        if (!validateToken(token, secret_refresh)) return false;

        // 2차 DB 토큰 검증
        Optional<Member> _member = memberRepository.findByUserId(getUserIdFromToken(token, secret_refresh));

        // Refresh 토큰이 존재하고 디코딩 결과값이 같다면 True 리턴
        return _member.isPresent() && token.equals(_member.get().getRefreshToken());
    }

    /**
     * 토큰 decode 후 userId 리턴
     */
    public String getUserIdFromToken(String token, String secret) {
        return this.decodeToken(token, secret).getClaim("userId").asString();
    }

    /**
     * 디코딩 토큰으로부터 claim 리턴
     */
    public String getClaimFromDecodedToken(DecodedJWT decodeToken, String claim){
        return decodeToken.getClaim(claim).toString();
    }
}
