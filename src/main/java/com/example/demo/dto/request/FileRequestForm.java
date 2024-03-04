package com.example.demo.dto.request;

import com.example.demo.entity.Board;
import com.example.demo.entity.File;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class FileRequestForm {
    private Long id;                // 파일 번호 (PK)
    @Setter
    private Long boardId;            // 게시글 번호 (FK)
    private String originalName;    // 원본 파일명
    private String savedName;        // 저장 파일명
    private long size;              // 파일 크기

    @Builder
    public FileRequestForm(String originalName, String savedName, long size) {
        this.originalName = originalName;
        this.savedName = savedName;
        this.size = size;
    }

    public File toEntity(Board board){
        File file = File.builder()
                .id(this.id)
                .board(board)
                .originalName(this.originalName)
                .savedName(this.savedName)
                .createdAt(LocalDateTime.now())
                .size(this.size)
                .build();
        return file;
    }
}
