package com.example.demo.dto;

import com.example.demo.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDto {
    private Long id;
//    @NotBlank(message = "제목은 필수 항목입니다.")
    @NotEmpty(message = "제목은 필수 항목입니다.")
    @Size(max = 200, message = "제목은 200자 이상 넘길 수 없습니다.")
    private String title;

//    @NotBlank(message="내용은 필수항목입니다.")
    @NotEmpty(message = "제목은 필수 항목입니다.")
    private String contents;
    private String createdAt;
    private Member author;
}
