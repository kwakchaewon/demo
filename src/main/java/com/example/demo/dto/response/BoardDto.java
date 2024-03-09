package com.example.demo.dto.response;

import com.example.demo.entity.Board;
import lombok.*;
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
    private String originalFile; // 작성자가 업로드한 파일명
    private String savedFile; // 서버 내부에서 관리하는 파일 명
    private String imgPath;
    /**
     * Entity -> Dto
     */
    public BoardDto(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.createdAt = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        this.memberId = board.getMember().getUserId();
        this.savedFile = board.getSavedFile();
        this.originalFile = board.getOriginalFile();

        if (board.getUpdatedAt()!=null){
            this.updatedAt = board.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        }
    }
}
