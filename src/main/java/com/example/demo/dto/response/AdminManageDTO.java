package com.example.demo.dto.response;

import com.example.demo.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminManageDTO {
    private Long id;
    private String userId;
    private String email;
    private String auth;

    public AdminManageDTO(Member member) {
        this.id = member.getId();
        this.userId = member.getUserId();
        this.email =  member.getEmail();
        this.auth = member.getGrantedAuth();
    }
}
