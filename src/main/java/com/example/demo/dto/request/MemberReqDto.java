package com.example.demo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
public class MemberReqDto {
    // @NotNull: Null 만 허용 x
    // @NotEmpty: Null, "" 허용 x
    // @NotBlank, Null, "", " " 허용 x

//    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$", message = "아이디가 올바르지 않습니다.</br>4~12자의 숫자,영문으로 생성 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$", message = "아이디: 4~12자의 숫자,영문을 입력해주세요.")
    private String userId;

//    @NotEmpty
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호: 8자 이상의 영문, 숫자, 특수문자를 입력해주세요")
//            message = "비밀번호가 올바르지 않습니다.</br>8자 이상의 영문, 숫자, 특수문자로 생성 가능합니다.")
    private String userPw;

//    @NotEmpty
    private String userPwCk;

//    @NotEmpty
    @Pattern(regexp = "^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-za-z0-9\\-]+",
            message = "이메일: 이메일 주소 양식을 입력해주세요.")
    private String email;

    @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
    public boolean isPasswordConfirmed() {
        return userPw != null && userPw.equals(userPwCk);
    }


    public MemberReqDto(String userId, String email, String userPw) {
        this.userId = userId;
        this.email = email;
        this.userPw = userPw;
//        this.userPwCk = userPwCk;
    }

    public void setEncodedPw(String encodedPw){
        this.userPw = encodedPw;
    }
}
