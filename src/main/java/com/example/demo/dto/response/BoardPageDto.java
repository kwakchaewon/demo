package com.example.demo.dto.response;

import com.example.demo.util.Pagination;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class BoardPageDto {
    private Page<BoardDto> boards;
    private Pagination pagination;

    public BoardPageDto(Page<BoardDto> boards, Pagination pagination) {
        this.boards = boards;
        this.pagination = pagination;
    }
}
