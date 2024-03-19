package com.example.demo.service;

import com.example.demo.entity.Member;
import com.example.demo.repository.MemberJpqlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberJpqlRepository memberJpqlRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<>();

//        Member memberEntity = memberRepository.findByUserId(username)   // JPA
        Member memberEntity = memberJpqlRepository.findMemberById(username) //JPQL
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 유저일 경우 ROLE_USER 권한 부여
        if (memberEntity.getUserId().equals(username) && memberEntity.getGrantedAuth().equals("ROLE_USER")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        else if (memberEntity.getUserId().equals(username) && memberEntity.getGrantedAuth().equals("ROLE_ADMIN")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        else if (memberEntity.getUserId().equals(username) && memberEntity.getGrantedAuth().equals("ROLE_SUPERVISOR")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SUPERVISOR"));
        }

        return new User(memberEntity.getUserId(), memberEntity.getUserPw(), authorities);
    }
}
