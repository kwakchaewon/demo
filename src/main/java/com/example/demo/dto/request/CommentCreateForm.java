package com.example.demo.dto.request;

import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreateForm {
    private String contents;

    public Comment toEntity(Member _member, Board _board){
        Comment comment = Comment.builder()
                .contents(this.contents)
                .createdAt(LocalDateTime.now())
                .member(_member)
                .board(_board)
                .build();
        return comment;
    }
}
