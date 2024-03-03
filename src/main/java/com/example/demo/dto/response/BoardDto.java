package com.example.demo.dto.response;

import com.example.demo.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
     * 수정 시, 제목, 내용 변경 및 memberId 값 추가
     */
    public void updateIdAndMemberId(Board board){
        this.id = board.getId();
        this.memberId = board.getMember().getUserId();
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
    }
}
