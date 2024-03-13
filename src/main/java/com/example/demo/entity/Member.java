package com.example.demo.entity;

import com.example.demo.dto.request.SignupForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.dto.response.MemberDto;
import lombok.*;
import net.bytebuddy.implementation.bind.annotation.Default;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String userPw;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
//    @OrderBy("createdAt desc")
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
//    @OrderBy("createdAt desc")
    private List<Board> boards;

//    @Column(nullable = false, columnDefinition = "DEFAULT 'ROLE_USER'")
    @Column(nullable = false)
    private String grantedAuth;

    public String refreshTokenUpdate(String token){
        this.refreshToken = token;
        return token;
    }

    public Member (SignupForm signupForm){
        this.userId = signupForm.getUserId();
        this.userPw = signupForm.getUserPw();
        this.email = signupForm.getEmail();
        this.createdAt = LocalDateTime.now();
    }

    public MemberDto ofMemberDto(){
        MemberDto memberDto =  MemberDto.builder()
                .id(this.id)
                .userId(this.userId)
                .build();

        return memberDto;
    }
}
