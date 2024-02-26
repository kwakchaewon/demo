package com.example.demo.entity;

import com.example.demo.dto.request.MemberReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) @NotBlank
    private String userId;

    @Column(nullable = false) @NotBlank
    private String userPw;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    private String refreshToken;

    public String refreshTokenUpdate(String token){
        this.refreshToken = token;
        return token;
    }

    public Member (MemberReqDto memberReqDto){
        this.userId = memberReqDto.getUserId();
        this.userPw = memberReqDto.getUserPw();
        this.email = memberReqDto.getEmail();
    }
}
