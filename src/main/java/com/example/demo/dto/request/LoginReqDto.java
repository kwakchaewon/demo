package com.example.demo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class LoginReqDto {
    @NotBlank
    private String userId;

    @NotBlank
    private String userPw;
}
