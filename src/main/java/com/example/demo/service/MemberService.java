package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberJpqlRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final MemberJpqlRepository memberJpqlRepository;
//    private final PasswordEncoder passwordEncoder;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<>();

//        Member memberEntity = memberRepository.findByUserId(username)   // JPA
        Member memberEntity = memberJpqlRepository.findMemberById(username) //JPQL
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (memberEntity.getUserId().equals(username)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new User(memberEntity.getUserId(), memberEntity.getUserPw(), authorities);
    }

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
    public void saveMember(MemberDto memberDto) throws Exception {

        // 1. JPA 기반 회원가입
//        Member newMember = Member.builder()
//                .email(memberDto.getEmail())
//                .userId(memberDto.getUserId())
//                .userPw(passwordEncoder.encode(memberDto.getUserPw()))
//                .build();
//
//        memberRepository.save(newMember);

        // 2. Native Query 기반 회원가입
        memberRepository.insertMember(memberDto.getUserId(), passwordEncoder.encode(memberDto.getUserPw()), memberDto.getEmail());
    }



//    private void validateMember(String userId) throws DuplicateMemberException {
//        if(memberRepository.existByUserName(userId)){
//            throw new DuplicateMemberException("중복된 id");
//        }
//    }

    public TokenDto login(LoginReqDto loginReqDto){
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

        // response 헤더에 ACCESS, REFRESH 토큰 재발급
//        setHeader(response, tokenDto);
//        return new GlobalResDto("Success Login", HttpStatus.OK.value());
    }

    private void setHeader(HttpServletResponse response, TokenDto tokenDto){
        response.addHeader("ACCESS_TOKEN", tokenDto.getAccessToken());
        response.addHeader("REFRESH_TOKEN" , tokenDto.getRefreshToken());
    }
}

