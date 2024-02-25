package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class MemberReqDto {

    @NotEmpty(message = "사용자ID는 필수항목입니다.")
    private String userId;

    @NotEmpty(message = "이메일은 필수항목입니다.") @Email
    private String email;

    @NotBlank
    private String userPw;

    private String userPwCk;

    public MemberReqDto(String userId, String email, String userPw, String userPwCk) {
        this.userId = userId;
        this.email = email;
        this.userPw = userPw;
        this.userPwCk = userPwCk;
    }

    public void setEncodedPw(String encodedPw){
        this.userPw = encodedPw;
    }
}
