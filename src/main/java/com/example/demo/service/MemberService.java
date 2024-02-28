package com.example.demo.service;

import com.example.demo.dto.request.LoginReqDto;
import com.example.demo.dto.request.MemberReqDto;
import com.example.demo.dto.response.MemberDto;
import com.example.demo.dto.response.TokenDto;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.util.JWTUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Member getMember(Long id) {
        Optional<Member> _member = this.memberRepository.findById(id);

        if (_member.isPresent()) {
            return _member.get();
        } else {
            throw new NoSuchElementException("Member is not found");
        }
    }

    public Member getMemberByUserId(String userId) {
        Optional<Member> _member = this.memberRepository.findByUserId(userId);

        if (_member.isPresent()) {
            return _member.get();
        } else {
            throw new NoSuchElementException("Member is not found");
        }
    }

    @Transactional
    public void saveMember(MemberReqDto memberReqDto) {

        // 1. JPA 기반 회원가입
//        Member newMember = Member.builder()
//                .email(memberDto.getEmail())
//                .userId(memberDto.getUserId())
//                .userPw(passwordEncoder.encode(memberDto.getUserPw()))
//                .build();
//
//        memberRepository.save(newMember);

        // 2. Native Query 기반 회원가입
        LocalDateTime createdAt = LocalDateTime.now();
        memberRepository.insertMember(memberReqDto.getUserId(), passwordEncoder.encode(memberReqDto.getUserPw()),
                memberReqDto.getEmail(),createdAt);
    }

    public TokenDto issueToken(LoginReqDto loginReqDto){
//        // 아이디 검사
//        Member member = memberRepository.findByUserId(loginReqDto.getUserId()).orElseThrow(() -> new RuntimeException("Not found Account"));
//
//        // 비밀번호 검사
//        if (!passwordEncoder.matches(loginReqDto.getUserPw(), member.getUserPw())){
//            throw new RuntimeException("Not matches Password");
//        }
        
        // 토큰 생성
        TokenDto tokenDto = jwtUtil.createAllToken(loginReqDto.getUserId());
        
        // 로그인 시 REFRESH 토큰 재발급 및 DB 업데이트
        Optional<Member> _member = memberRepository.findByUserId(loginReqDto.getUserId());
        _member.get().refreshTokenUpdate(tokenDto.getRefreshToken());
        memberRepository.save(_member.get());

        return tokenDto;
    }

    public UserDetails setAuth(LoginReqDto loginReqDto){
        String userId = loginReqDto.getUserId();
        String userPw = loginReqDto.getUserPw();

        UserDetails loginUser = userDetailsService.loadUserByUsername(userId); //1. userId로 패스워드 가져오기
        Authentication authentication = authenticationManager.authenticate(     //2. 가져온 패스워드와 입력한 비밀번호로 검증
                new UsernamePasswordAuthenticationToken(loginUser, userPw)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);   // 3. 검증 통과 후 authentication 세팅

        return loginUser;
    }

    @Transactional(readOnly = true)
    public  Map<String, String>validateHandling(BindingResult bindingResult) {
        Map<String, String> validatorResult = new HashMap<>();

        for(FieldError error : bindingResult.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }

        return validatorResult;
    }
}

