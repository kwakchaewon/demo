package com.example.demo.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class FileRequestForm {
    private Long id;                // 파일 번호 (PK)
    @Setter
    private Long boardId;            // 게시글 번호 (FK)
    private String originalName;    // 원본 파일명
    private String saveName;        // 저장 파일명
    private long size;              // 파일 크기

    @Builder
    public FileRequestForm(String originalName, String saveName, long size) {
        this.originalName = originalName;
        this.saveName = saveName;
        this.size = size;
    }
}
