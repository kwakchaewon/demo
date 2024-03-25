package com.example.demo.controller;

import com.example.demo.dto.request.LoginReqDto;
import com.example.demo.dto.request.MemberAuthUpdateForm;
import com.example.demo.dto.request.SignupForm;
import com.example.demo.dto.response.PagingResponse;
import com.example.demo.dto.response.TokenDto;
import com.example.demo.service.MemberService;
import com.example.demo.util.JWTUtil;
import com.example.demo.util.SecurityUtils;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 로그인 & 회원가입 컨트롤러
 * 로그인, 회원가입, 액세스 토큰 재발급
 */
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

    /**
     * 회원가입
     * 아이디, 이메일 중복 검사 (실패시, 400 반환)
     */
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

    /**
     * 로그인 및 액세스, 리프레쉬 토큰 부여
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginReqDto loginReqDto) throws CustomException {
        UserDetails loginUser = memberService.setAuth(loginReqDto.getUserId(), loginReqDto.getUserPw());   // 1. 로그인 검증 및 인증 객체 설정
        TokenDto tokenDto = memberService.issueToken(loginUser);    // 2. 토큰 발급 및 관련 로직
        return ResponseEntity.ok(tokenDto);
    }

    /**
     * 액세스 토큰 재발급
     */
    @GetMapping("/refresh")
    public TokenDto refresh(HttpServletResponse response) {
        String accessToken = response.getHeader("ACCESS_TOKEN");
        Date accessExpired = jwtUtil.decodeToken(accessToken, secret_access).getExpiresAt();
        String userId = jwtUtil.decodeToken(accessToken, secret_access).getClaim("userId").toString();
        String authority = jwtUtil.decodeToken(accessToken, secret_access).getClaim("authority").toString();
        TokenDto tokenDto = new TokenDto(accessToken, accessExpired, userId, authority);
        return tokenDto;
    }

    /**
     * 사용자 권한 수정
     */
    @PutMapping("/{id}/auth")
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ResponseEntity updateMemberAuth(@PathVariable("id") Long id,
                                           @RequestBody MemberAuthUpdateForm memberAuthUpdateForm) {
        String auth = memberAuthUpdateForm.getAuth();
        this.memberService.updateAuth(id, auth);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 관리자 페이지 사용자 (+관리자) 조회
     */
    @GetMapping(value = "/list")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_ADMIN')")
    public PagingResponse pagingMemberList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable,
            Authentication authentication){

        // admin: 일반유저 조회 권한
        if (SecurityUtils.isAdmin(authentication))
        {
            return memberService.getMembers(pageable);
        }
        // super: 관리자 + 일반 유저 조회 권한
        else if (SecurityUtils.isSupervisor(authentication))
        {
            return memberService.getMembersIncludingAdmin(pageable);
        }
        else {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }
}
