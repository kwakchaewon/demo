package com.example.demo.controller;

import com.example.demo.dto.LoginReqDto;
import com.example.demo.dto.MemberDto;
import com.example.demo.dto.TokenDto;
import com.example.demo.service.MemberService;
import com.example.demo.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/member")
public class MemberController {
    private final JWTUtil jwtUtil;
    private final MemberService memberService;

//    @PostMapping("/login")
//    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> paramMap) {
//        String userId = paramMap.get("user_id");
//        String userPw = paramMap.get("user_pw");
//
//        UserDetails loginUser = memberService.loadUserByUsername(userId); //1. userId로 패스워드 가져오기
//
//        Authentication authentication = authenticationManager.authenticate(     //2. 가져온 패스워드와 입력한 비밀번호로 검증
//                new UsernamePasswordAuthenticationToken(loginUser, userPw)
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);   // 3. 검증 통과 후 authentication 세팅
//
//        String accessToken = jwtUtil.createToken(loginUser.getUsername(), loginUser.getUsername());     // 4. accessToken 생성
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("user_id", loginUser.getUsername());
//        result.put("user_token", accessToken);
//        result.put("user_role", loginUser.getAuthorities().stream().findFirst().get().getAuthority());
//
//        return ResponseEntity.ok(result);
//    }

    /**
     * REFRESH TOKEN 활용 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginReqDto loginReqDto) {

        UserDetails loginUser = memberService.setAuth(loginReqDto);   // 1. 로그인 검증 및 auth 세팅
        TokenDto tokenDto = memberService.issueToken(loginReqDto);    // 2. 토큰 발급 및 관련 로직

        Map<String, Object> result = new HashMap<>();

        result.put("user_id", loginUser.getUsername());
        result.put("user_role", loginUser.getAuthorities().stream().findFirst().get().getAuthority());
        result.put("ACCESS_TOKEN", tokenDto.getAccessToken());
        result.put("REFRESH_TOKEN", tokenDto.getRefreshToken());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/signup")
    public String signUp(@RequestBody MemberDto memberDto) throws Exception{
        memberService.saveMember(memberDto);
        return "success";
    }
}
