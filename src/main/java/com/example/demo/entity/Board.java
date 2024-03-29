package com.example.demo.entity;

import com.example.demo.dto.request.BoardUpdateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.util.FileStore;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contents;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @ManyToOne //(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
//    @OrderBy("createdAt desc")
    private List<Comment> comments;

    @Column
    private String originalFile;

    @Column
    private String savedFile;

    public void updateTitleAndContents(BoardUpdateForm boardUpdateForm) {
        this.title = boardUpdateForm.getTitle();
        this.contents = boardUpdateForm.getContents();
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

    public BoardDto of() {
        BoardDto boardDto = BoardDto.builder()
                .id(this.getId())
                .title(this.getTitle())
                .contents(this.getContents())
                .createdAt(this.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")))
                .originalFile(this.getOriginalFile())
                .memberId(this.getMember().getUserId())
//                .savedFile(this.getSavedFile())
                .build();

        if (updatedAt != null) {
            boardDto.setUpdatedAt(this.updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")));
        }

        return boardDto;
    }

    public void resetFile() {
        this.setSavedFile(null);
        this.setOriginalFile(null);
    }

    public void updateFile(String originalFile, String savedFile) {
        this.originalFile = originalFile;
        this.savedFile = savedFile;
    }
}
