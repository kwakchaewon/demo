package com.example.demo.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.dto.request.SignupForm;
import com.example.demo.dto.response.*;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.util.JWTUtil;
import com.example.demo.util.Pagination;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MemberService {
    @Autowired
    private JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.secret_access}")
    private String secret_access;


    @Autowired
    private PasswordEncoder passwordEncoder;

    public PagingResponse<MemberAdminDto> getMembers(Pageable pageable) {

        Page<MemberAdminDto> members = memberRepository.findAllByGrantedAuthOrderByIdDesc("ROLE_USER", pageable);

        Pagination pagination = new Pagination(
                (int) members.getTotalElements()
                , pageable.getPageNumber() + 1
                , pageable.getPageSize()
                , 10
        );

        return new PagingResponse<>(members, pagination);
    }

    public PagingResponse<MemberSuperDto> getMembersIncludingAdmin(Pageable pageable) {
//        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        Page<MemberSuperDto> members = memberRepository.findUserIncludingAdmin(pageable);

        Pagination pagination = new Pagination(
                (int) members.getTotalElements()
                , pageable.getPageNumber() + 1
                , pageable.getPageSize()
                , 10
        );

        return new PagingResponse<>(members, pagination);
    }

    public Member getMember(Long id) {
        Optional<Member> _member = this.memberRepository.findById(id);

        if (!_member.isPresent()) throw new NoSuchElementException("Member is not found");

        return _member.get();
    }

    public Member getMemberByUserId(String userId) {
        return this.memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void saveMember(SignupForm signupForm) throws CustomException {
        // Native Query 기반 회원가입
        LocalDateTime createdAt = LocalDateTime.now();
        try {
            memberRepository.insertMember(signupForm.getUserId(), passwordEncoder.encode(signupForm.getUserPw()), signupForm.getEmail(), createdAt, "ROLE_USER");
        } catch (Exception e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.UNKNOWN_ERROR);
        }
    }

    public TokenDto issueToken(UserDetails loginUser) throws CustomException {

        // 1. userId 추출
        String userId = loginUser.getUsername();

        // 2. 권한 추출
        String authority = loginUser.getAuthorities().stream().findFirst().get().getAuthority();

        // 3. 토큰 생성
        TokenDto tokenDto = jwtUtil.createTokenDto(userId, authority);

        // 4. 새로운 REFRESH 토큰 DB 업데이트
        Member _member = this.getMemberByUserId(userId);
        _member.setRefreshToken(tokenDto.getRefreshToken());
        memberRepository.save(_member);

        return tokenDto;
    }

    public UserDetails setAuth(String userId, String userPw) {
        UserDetails loginUser = userDetailsService.loadUserByUsername(userId); //1. userId로 패스워드 가져오기
        Authentication authentication = authenticationManager.authenticate(     //2. 가져온 패스워드와 입력한 비밀번호로 검증
                new UsernamePasswordAuthenticationToken(loginUser, userPw)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);   // 3. 검증 통과 후 authentication 세팅
        return loginUser;
    }

    /* 아이디 중복 확인 */
    public boolean checkUseridDuplication(SignupForm signupForm) {
        return memberRepository.existsByUserId(signupForm.getUserId());
    }

    /* 이메일 중복 확인 */
    public boolean checkEmailDuplication(SignupForm signupForm) {
        return memberRepository.existsByEmail(signupForm.getEmail());
    }

    @Transactional
    public void deleteMemberById(Long id) throws CustomException {
        try {
            this.memberRepository.deleteById(id);
        } catch (NullPointerException e) {
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.MEMBER_NOTFOUND);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.UNKNOWN_ERROR);
        }
    }

    // 사용자 권한 수정
    public Member updateAuth(Long id, String auth) {
        Member member = this.getMember(id);
        member.setGrantedAuth(auth);
        return this.memberRepository.save(member);
    }

    public TokenDto refreshAccessToken(HttpServletResponse response) {
        String accessToken = response.getHeader("ACCESS_TOKEN"); // 1. 액세스 토큰

        DecodedJWT decodedJWT = jwtUtil.decodeToken(accessToken, secret_access); // 토큰 디코딩

        String userId = jwtUtil.getClaimFromDecodedToken(decodedJWT, "userId"); // 유저아이디
        String authority = jwtUtil.getClaimFromDecodedToken(decodedJWT, "authority");   // 권한
        Date accessExpired = decodedJWT.getExpiresAt(); // 만료기간
        return new TokenDto(accessToken, accessExpired, userId, authority);
    }
}

