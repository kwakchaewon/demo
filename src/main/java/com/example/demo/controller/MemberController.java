package com.example.demo.controller;

import com.example.demo.dto.request.LoginReqDto;
import com.example.demo.dto.request.SignupForm;
import com.example.demo.dto.response.TokenDto;
import com.example.demo.service.MemberService;
import com.example.demo.util.CustomVal;
import com.example.demo.util.JWTUtil;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final JWTUtil jwtUtil;
    private final CustomVal customVal;

    @Value("${jwt.secret_access}")
    private String secret_access;

    @Value("${jwt.secret_refresh}")
    private String secret_refresh;

    /**
     * REFRESH TOKEN 활용 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginReqDto loginReqDto) {

        UserDetails loginUser = memberService.setAuth(loginReqDto);   // 1. 로그인 검증 및 auth 세팅
        TokenDto tokenDto = memberService.issueToken(loginReqDto);    // 2. 토큰 발급 및 관련 로직
//        long ACCESS_EXP = jwtUtil.decodeToken(tokenDto.getAccessToken(), secret_access).getExpiresAt().getTime();
        int ACCESS_EXP = 5 / (24 * 60);
        int REFRESH_EXP = 7;

        Map<String, Object> result = new HashMap<>();

        result.put("user_role", loginUser.getAuthorities().stream().findFirst().get().getAuthority());
        result.put("ACCESS_TOKEN", tokenDto.getAccessToken());
        result.put("REFRESH_TOKEN", tokenDto.getRefreshToken());
        result.put("ACCESS_EXP", ACCESS_EXP);
        result.put("REFRESH_EXP", REFRESH_EXP);

        return ResponseEntity.ok(result);
    }

    /**
     * 액세스 토큰 재발급
     */
    @GetMapping("/refresh")
    public String refresh(String refreshToken, HttpServletResponse response){
        String token = response.getHeader("ACCESS_TOKEN");
        return token;
    }

    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody SignupForm signupForm) throws CustomException {
        // 아이디 중복 검사
        if (memberService.checkUseridDuplication(signupForm)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.USERID_DUPLICATED);
        }
        // 이메일 중복 검사
        else if (memberService.checkEmailDuplication(signupForm)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.EMAIL_DUPLICATED);
        } 
        // 회원가입 성공
        else {
            memberService.saveMember(signupForm);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
