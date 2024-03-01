package com.example.demo.dto.response;

import com.example.demo.entity.Comment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@AllArgsConstructor
@Data
public class CommentDto {
    private Long id;
    private String contents;
    private String createdAt;
    private String memberId;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.contents = comment.getContents();
        this.createdAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        this.memberId = comment.getMember().getUserId();
    }
}
