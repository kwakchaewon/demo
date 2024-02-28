package com.example.demo.entity;

import com.example.demo.dto.response.BoardDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contents;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public void update(BoardDto boardDto){
        this.title = title;
        this.contents = contents;
        this.updatedAt = LocalDateTime.now();
    }

    public Board(String title, String contents) {
        this.title = title;
        this.contents = contents;
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Board(String title, String contents, Member member) {
        this.title = title;
        this.contents = contents;
        this.createdAt = LocalDateTime.now();
        this.member = member;
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
