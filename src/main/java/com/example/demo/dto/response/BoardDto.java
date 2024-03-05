package com.example.demo.dto.response;

import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDto {
    private Long id;
    private String title;
    private String contents;
    private String createdAt, updatedAt;
    private String memberId;

    /**
     * Entity -> Dto
     */
    public BoardDto(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.createdAt = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        this.memberId = board.getMember().getUserId();

        if (board.getUpdatedAt()!=null){
            this.updatedAt = board.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        }
    }

    /**
     * Projection
     */
    public BoardDto(Long id, String title, String contents, LocalDateTime createdAt, LocalDateTime updatedAt, Member member){
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        this.memberId = member.getUserId();
        if (updatedAt!=null){
            this.updatedAt =updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        }
    }
}
