package com.example.demo.dto.response;

import com.example.demo.entity.Member;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class MemberSuperDto {
    private Long id;
    private String userId;
    private String email;
    private String createdAt;
    private String auth;

    public MemberSuperDto(Member member) {
        this.id = member.getId();
        this.userId = member.getUserId();
        this.email = member.getEmail();
        this.createdAt = member.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.auth = member.getGrantedAuth();
    }
}
