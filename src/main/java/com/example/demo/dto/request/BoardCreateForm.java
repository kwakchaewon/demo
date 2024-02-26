package com.example.demo.dto.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardCreateForm {
    @NotEmpty(message = "제목은 필수 항목입니다.")
    @Size(max = 200)
    private String title;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String contents;
}
