package com.example.demo.dto.response;

import com.example.demo.entity.Board;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @JsonIgnore
    public boolean isWriter(String id){
        return this.memberId.equals(id);
    }

    @JsonIgnore
    public boolean isFileExists(){
        return !this.savedFile.isEmpty();
    }
}
