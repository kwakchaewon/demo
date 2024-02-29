package com.example.demo.dto.request;

import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class BoardCreateForm {
    @NotEmpty(message = "제목은 필수 항목입니다.")
    @Size(max = 200)
    private String title;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String contents;

    public Board toEntity(Member _member){
        Board board = Board.builder()
                .title(this.getTitle())
                .contents(this.getContents())
                .createdAt(LocalDateTime.now())
                .member(_member)
                .build();

        return board;
    }
}