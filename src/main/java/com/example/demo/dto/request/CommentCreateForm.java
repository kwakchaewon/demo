package com.example.demo.dto.request;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentCreateForm {
    private String title;
    private String contents;

    public Comment toEntity(Member _member){
        Comment comment = Comment.builder()
                .contents(this.getTitle())
                .createdAt(LocalDateTime.now())
                .member(_member)
                .build();
        return comment;
    }
}
