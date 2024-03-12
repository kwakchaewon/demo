package com.example.demo.dto.response;

import com.example.demo.entity.Member;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {
    @Getter
    private Long id;
    @Getter
    private String userId;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.userId = member.getUserId();
    }
}
