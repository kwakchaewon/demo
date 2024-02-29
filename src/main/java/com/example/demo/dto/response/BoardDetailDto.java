package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDetailDto {
    private Long id;
    private String title;
    private String contents;
    private String createdAt;
    private MemberDto memberDto;
}
