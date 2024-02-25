package com.example.demo.dto;

import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@Builder
public class BoardResponse {
    private Long id;
    private boolean success;
    private String message;
//    private HashMap<String,Object> data;
    private Board board;

    public BoardResponse(Long id, boolean success, String message, Board board) {
        this.id = id;
        this.success = success;
        this.message = message;
        this.board = board;
    }
}
