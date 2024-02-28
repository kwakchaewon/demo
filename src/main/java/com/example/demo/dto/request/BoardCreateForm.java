package com.example.demo.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardCreateForm {
    @NotBlank(message = "제목을 입력하세요.")
    private String title;

    @NotBlank(message="내용을 입력하세요.")
    private String contents;
}
