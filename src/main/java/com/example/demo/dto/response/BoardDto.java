package com.example.demo.dto.response;

import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.format.DateTimeFormatter;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDto {
    private Long id;

    @NotEmpty(message = "제목은 필수 항목입니다.")
    @Size(max = 200, message = "제목은 200자 이상 넘길 수 없습니다.")
    private String title;

    @NotEmpty(message = "제목은 필수 항목입니다.")
    private String contents;
    private String createdAt;
    private String updatedAt;
    private Member member;


    /**
     * 수정 시, 제목, 내용 제외한 나머지 필드 업데이트
     */
    public void updateIdAndAuthor(Board board){
        this.id = board.getId();
        this.member = board.getMember();
        this.createdAt = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        this.updatedAt = board.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }

    public Board toEntity(){
        return Board.builder()
                .title(title)
                .contents(contents)
                .member(member)
                .build();
    }
}
