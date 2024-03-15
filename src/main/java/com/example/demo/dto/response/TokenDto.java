package com.example.demo.dto.response;

import com.example.demo.util.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String accessToken;  // 액세스 토큰
    private float accessTime;  // 액세스 토큰 만료시간
    private String refreshToken;  // 리프레쉬 토큰
    private float refreshTime;  // 리프레쉬 토큰 만료시간
    private String authority; // 권한

    public TokenDto(String accessToken, String refreshToken, Date accessExpired, Date refreshExpired, String authority) {
        Date now = new Date();

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

        // 현재 시간과의 단위차이 계산 후 시간 단위로 변환
        this.accessTime = (float) (accessExpired.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
        this.refreshTime = (float) (refreshExpired.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
        this.authority = authority;
    }

    public TokenDto(String accessToken, Date accessExpired, String authority) {
        Date now = new Date();
        this.accessToken = accessToken;
        this.accessTime = (float) (accessExpired.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
        this.authority = authority;
    }
}
