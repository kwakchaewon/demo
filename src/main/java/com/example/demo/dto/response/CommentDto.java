package com.example.demo.dto.response;

import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
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

    public CommentDto(Long id, String contents, LocalDateTime createdAt, LocalDateTime updatedAt, Board board, Member member) {
        this.id = id;
        this.contents = contents;
        this.createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        this.memberId = member.getUserId();
        this.boardId = board.getId();
        if (updatedAt!=null){
            this.updatedAt =updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        }
    }
}
