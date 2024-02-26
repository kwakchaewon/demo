package com.example.demo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.dto.TokenDto;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTUtil {
    private final MemberRepository memberRepository;
    @Value("${jwt.secret}")
    private String secret;

    private static final Date ACCESS_TIME =  new Date(System.currentTimeMillis()+(60 * 60 * 1000L)); // 1시간
    private static final Date REFRESH_TIME =  new Date(System.currentTimeMillis()+(7 * 24 * 60 * 60 * 1000L)); // 1주일
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    public String createToken(String userId) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("vue-board")
                .withClaim("userId", userId)
//                .withClaim("userName", userName)
                .withIssuedAt(new Date())
//                .withExpiresAt(new Date(System.currentTimeMillis()+3000)) // 만료시간: 30 sec
                .withExpiresAt(new Date(System.currentTimeMillis()+1_800_000)) // 만료시간: 30 min
                .sign(algorithm);
    }

    public DecodedJWT decodeToken(String token) {
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
    public String createToken2(String userId, String type){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Date expTime = type.equals("ACCESS_TOKEN") ? ACCESS_TIME : REFRESH_TIME;

        return JWT.create()
                .withIssuer("vue-board")
                .withClaim("userId", userId) // 토큰 주체
                .withIssuedAt(new Date())
                .withExpiresAt(expTime)
                .sign(algorithm);
    }

    // 토큰 생성
    public TokenDto createAllToken(String userId) {
        return new TokenDto(createToken2(userId, "ACCESS_TOKEN"), createToken2(userId, "REFRESH_TOKEN"));
    }

    //토큰 검증
    public Boolean validateToken(String token){
        try {
            this.decodeToken(token);
            return true;
        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    // refreshToken 토큰 검증
    // db에 저장되어 있는 token과 비교
    public Boolean validateRefreshToken(String token){

        // 1차 토큰 검증
        if(!validateToken(token)) return false;

        // 2차 DB 토큰 검증
        Optional<Member> _member = memberRepository.findByUserId(getUserIdFromToken(token));

        // Refresh 토큰이 존재하고 디코딩 결과값이 같다면 True 리턴
        return _member.isPresent() && token.equals(_member.get().getRefreshToken());
    }

    /**
     * 토큰 decode 후 userId 리턴
     */
    public String getUserIdFromToken(String token) {
        return this.decodeToken(token).getClaim("userId").asString();
    }

    // 인증 객체 생성
//    public Authentication createAuthentication(String email) {
//        UserDetails userDetails = memberService.loadUserByUsername(email);
//        // spring security 내에서 가지고 있는 객체입니다. (UsernamePasswordAuthenticationToken)
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }

    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS_TOKEN, accessToken);
    }

    // 리프레시 토큰 헤더 설정
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader(REFRESH_TOKEN, refreshToken);
    }

    // header 토큰을 가져오는 기능
    public String getHeaderToken(HttpServletRequest request, String type) {
        return type.equals("ACCESS_TOKEN") ? request.getHeader(ACCESS_TOKEN) :request.getHeader(REFRESH_TOKEN);
    }
}
