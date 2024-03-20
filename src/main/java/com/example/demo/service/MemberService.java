package com.example.demo.service;

import com.example.demo.dto.request.LoginReqDto;
import com.example.demo.dto.request.SignupForm;
import com.example.demo.dto.response.AdminManageDTO;
import com.example.demo.dto.response.AdminMemberDto;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.dto.response.TokenDto;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.util.JWTUtil;
import com.example.demo.util.Pagination;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {
    @Autowired
    private JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    public List<AdminMemberDto> getMemberList(){
//        return memberRepository.findAllAdminMemberDtoByGrantedAuth("ROLE_USER");
//    }

    public Map<String, Object> getMemberList2(Pageable pageable){
        Map<String, Object> data = new HashMap();

        Page<AdminMemberDto> memberList = memberRepository.findAllAdminMemberDtoByGrantedAuthOrderByIdDesc("ROLE_USER" ,pageable);

        Pagination pagination = new Pagination(
                (int) memberList.getTotalElements()
                , pageable.getPageNumber() + 1
                , pageable.getPageSize()
                , 10
        );

        data.put("boards", memberList);
        data.put("pagination", pagination);

        return data;
    }

    public Map<String, Object> getAdminList(Pageable pageable){
        Map<String, Object> data = new HashMap();


        Page<AdminManageDTO> adminList = memberRepository.findUserOrAdminOrderedByIdDesc(pageable);

        Pagination pagination = new Pagination(
                (int) adminList.getTotalElements()
                , pageable.getPageNumber() + 1
                , pageable.getPageSize()
                , 10
        );

        data.put("members", adminList);
        data.put("pagination", pagination);

        return data;
    }


    public Member getMember(Long id) {
        Optional<Member> _member = this.memberRepository.findById(id);

        if (_member.isPresent()) {
            return _member.get();
        } else {
            throw new NoSuchElementException("Member is not found");
        }
    }

    public Member getMemberByUserId(String userId) throws CustomException {
        Optional<Member> _member = this.memberRepository.findByUserId(userId);

        if (_member.isPresent()) {
            return _member.get();
        } else {
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.USER_INVALID);
        }
    }

    @Transactional
    public void saveMember(SignupForm signupForm) throws CustomException {
        // Native Query 기반 회원가입
        LocalDateTime createdAt = LocalDateTime.now();
        try {
            memberRepository.insertMember(signupForm.getUserId(), passwordEncoder.encode(signupForm.getUserPw()), signupForm.getEmail(),createdAt, "ROLE_USER");
        }catch (Exception e){
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

    /* 아이디 중복 확인 */
    public boolean checkUseridDuplication(SignupForm signupForm){
        return memberRepository.existsByUserId(signupForm.getUserId());
    }

    /* 이메일 중복 확인 */
    public boolean checkEmailDuplication(SignupForm signupForm){
        return memberRepository.existsByEmail(signupForm.getEmail());
    }

    @Transactional
    public void deleteMemberById(Long id) throws CustomException {
        try {
            this.memberRepository.deleteById(id);
        }
        catch (NullPointerException e){
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.MEMBER_NOTFOUND);
        }
        catch (Exception e){
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.UNKNOWN_ERROR);
        }
    }

    // 사용자 권한 수정
    public Member updateAuth(Long id, String auth){
        Member member = this.getMember(id);
        member.setGrantedAuth(auth);
        return this.memberRepository.save(member);
    }
}

