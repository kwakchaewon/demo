package com.example.demo.dto.response;

import com.example.demo.entity.Board;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BoardDto {
    private Long id;
    private String title;
    private String contents;
    private String createdAt, updatedAt;
    private String memberId;
    private String errMsg;

    /**
     * 수정 시, 제목, 내용 제외한 나머지 필드 업데이트
     */
    public void updateIdAndAuthor(Board board){
        this.id = board.getId();
        this.updatedAt = board.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }

    /**
     * Entity -> Dto
     */
    public BoardDto(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.createdAt = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        this.memberId = board.getMember().getUserId();
//        this.comments = board.getComments().stream().map(CommentDto::new).collect(Collectors.toList());
    }

//    public BoardDto(Board board){
//        this.id = board.getId();
//        this.title = board.getTitle();
//        this.writer = board.getMember().getUserId();
//        this.contents = board.getContents();
//        this.createdAt = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
//        this.memberId = board.getMember().getId();
//        this.comments = board.getComments().stream().map(CommentDto::new).collect(Collectors.toList());
//    }

    public BoardDto(String errorMsg) {
        this.errMsg = errorMsg;
    }
}
