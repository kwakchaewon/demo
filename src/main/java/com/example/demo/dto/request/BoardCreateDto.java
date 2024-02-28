package com.example.demo.dto.request;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardCreateDto {
    @NotBlank(message = "제목을 입력하세요.")
    private String title;

    @NotBlank(message="내용을 입력하세요.")
    private String contents;
}
