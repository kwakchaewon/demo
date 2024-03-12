package com.example.demo.dto.response;

import com.example.demo.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminMemberDto {
    private Long id;
    private String userId;
    private String email;
    private String createdAt;

    public AdminMemberDto(Member member) {
        this.id = member.getId();
        this.userId = member.getUserId();
        this.email = member.getEmail();
        this.createdAt = member.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
    }
}
