package com.example.demo.controller;

import com.example.demo.dto.request.LoginReqDto;
import com.example.demo.dto.request.MemberReqDto;
import com.example.demo.dto.response.TokenDto;
import com.example.demo.service.MemberService;
import com.example.demo.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
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
        int ACCESS_EXP = 1/(24*60);
        int REFRESH_EXP = 7;

        Map<String, Object> result = new HashMap<>();

        result.put("user_role", loginUser.getAuthorities().stream().findFirst().get().getAuthority());
        result.put("ACCESS_TOKEN", tokenDto.getAccessToken());
        result.put("REFRESH_TOKEN", tokenDto.getRefreshToken());
        result.put("ACCESS_EXP", ACCESS_EXP);
        result.put("REFRESH_EXP", REFRESH_EXP);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/signup")
    public ResponseEntity<HashMap<String,Object>> signUp(@Valid @RequestBody MemberReqDto memberReqDto,
                                              BindingResult bindingResult) {
        HashMap<String,Object> response = new HashMap<>();

        // @Valid 기반 회원가입 유효성 검사
        if(bindingResult.hasErrors()){
            Map<String, String> validatorResult = memberService.validateHandling(bindingResult);
            response.put("errorMessages",validatorResult);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // 아이디 중복시,
        else if (memberService.checkUseridDuplication(memberReqDto)){
            Map<String, String> validatorResult = new HashMap<>();
            validatorResult.put("duplicate_userId", "이미 존재하는 아이디 입니다.");
            response.put("errorMessages",validatorResult);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // 이메일 중복시
        else if (memberService.checkEmailDuplication(memberReqDto)){
            Map<String, String> validatorResult = new HashMap<>();
            validatorResult.put("duplicate_email", "이미 존재하는 이메일 입니다.");
            response.put("errorMessages",validatorResult);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            memberService.saveMember(memberReqDto);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    // 검증이 포함된 access 로그인
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

}
