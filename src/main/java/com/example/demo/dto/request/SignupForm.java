package com.example.demo.dto.request;
import lombok.Getter;
import javax.validation.constraints.*;

@Getter
public class SignupForm {
    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$", message = "아이디: 4~12자의 숫자,영문을 입력해주세요.")
    private String userId;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호: 8자 이상의 영문, 숫자, 특수문자를 입력해주세요")
    private String userPw;

    private String userPwCk;

    @Pattern(regexp = "^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-za-z0-9\\-]+",
            message = "이메일: 이메일 주소 양식을 입력해주세요.")
    private String email;

//    @JsonCreator
//    public MemberReqDto (@JsonProperty("userId") String userId,
//                         @JsonProperty("userPw") String userPw,
//                         @JsonProperty("email") String email,
//                         @JsonProperty("userPw") String userPwCk
//    ){
//        this.userId= userId;
//        this.userId= userPw;
//        this.userId= email;
//        this.userId= userPwCk;
//    }

    @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
    public boolean isPasswordConfirmed() {
        return userPw != null && userPw.equals(userPwCk);
    }

//    public Member toEntity(){
//        Member member = Member.builder()
//                .userId(this.getUserId())
//                .userPw(passwordEncoder.encode(this.getUserPw()))
//                .email(this.getEmail())
//                .createdAt(LocalDateTime.now())
//                .build();
//        return member;
//    }
}
