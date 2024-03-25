package com.example.demo.dto.response;

import com.example.demo.entity.Member;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class MemberAdminDto {
    private Long id;
    private String userId;
    private String email;
    private String createdAt;

    public MemberAdminDto(Long id, String userId, String email, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.createdAt = createdAt;
    }

    public MemberAdminDto(Member member){
        this.id = member.getId();
        this.userId = member.getUserId();
        this.email = member.getEmail();
        this.createdAt = member.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
