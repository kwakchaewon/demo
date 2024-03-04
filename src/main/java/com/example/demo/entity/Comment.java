package com.example.demo.entity;

import com.example.demo.dto.request.CommentCreateForm;
import com.example.demo.dto.response.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String contents;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Comment(String contents, Member _member, Board _board) {
        this.contents = contents;
        this.createdAt = LocalDateTime.now();
        this.member = _member;
        this.board = _board;
    }

    public CommentDto of(){
        CommentDto commentDto = CommentDto.builder()
                .id(this.getId())
                .contents(this.getContents())
                .createdAt(this.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")))
                .memberId(this.getMember().getUserId())
                .build();
    return commentDto;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", contents='" + contents + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", board=" + board.getId() +
                ", member=" + member.getUserId() +
                '}';
    }

    public void update(CommentCreateForm commentCreateForm){
        this.contents =  commentCreateForm.getContents();
        this.updatedAt = LocalDateTime.now();
    }
}
