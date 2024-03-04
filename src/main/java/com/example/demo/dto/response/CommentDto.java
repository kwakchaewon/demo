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
@Builder
public class CommentDto {
    private Long id;
    private String contents;
    private String createdAt, updatedAt;
    private String memberId;
    private Long boardId;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.contents = comment.getContents();
        this.createdAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        this.memberId = comment.getMember().getUserId();
        this.boardId = comment.getBoard().getId();

        if (comment.getUpdatedAt()!=null){
            this.updatedAt = comment.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        }
    }
}
