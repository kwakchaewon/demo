package com.example.demo.dto.request;

import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentReqDto {
    private Long id;
    private String title;
    private String contents;
    private Member member;
    private Board board;

    public Comment toEntity(){
        Comment comment = Comment.builder()
                .id(id)
                .contents(contents)
                .createdAt(LocalDateTime.now())
                .member(member)
                .board(board)
                .build();
        return comment;
    }
}
