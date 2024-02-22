package com.example.demo.service;

import com.example.demo.dto.MemberDto;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import javassist.bytecode.DuplicateMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
//    private final PasswordEncoder passwordEncoder;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<>();

        Member memberEntity = memberRepository.findByUserId(username)
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
//        this.validateMember(memberDto.getUserId());
        Member newMember = Member.builder()
                .email(memberDto.getEmail())
                .userId(memberDto.getUserId())
                .userPw(passwordEncoder.encode(memberDto.getUserPw()))
                .build();

    memberRepository.save(newMember);
    }

//    private void validateMember(String userId) throws DuplicateMemberException {
//        if(memberRepository.existByUserName(userId)){
//            throw new DuplicateMemberException("중복된 id");
//        }
//    }
}

