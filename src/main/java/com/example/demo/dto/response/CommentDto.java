package com.example.demo.dto.response;

import com.example.demo.entity.Comment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CommentDto {
    private Long id;
    private String contents;
    private String createdAt;
    private String memberId;
    private Long boardId;

    public CommentDto(Comment comment){
        this.id = comment.getId();
        this.contents = comment.getContents();
        this.createdAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        this.memberId = comment.getMember().getUserId();
        this.boardId = comment.getBoard().getId();
    }
}
