package com.example.demo.dto.request;

import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import net.bytebuddy.implementation.bind.annotation.Default;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateForm {
    private String title;
    private String contents;
    //    private List<MultipartFile> files = new ArrayList<>();
    private Optional<MultipartFile> file = Optional.empty();

    public Board toEntityWithFile(Member _member, String savedFileName) {

            Board board = Board.builder()
                    .title(this.getTitle())
                    .contents(this.getContents())
                    .createdAt(LocalDateTime.now())
                    .member(_member)
                    .originalFile(this.getFile().get().getOriginalFilename())
                    .savedFile(savedFileName)
                    .build();

            return board;
    }

    public Board toEntity(Member _member) {

        Board board = Board.builder()
                .title(this.getTitle())
                .contents(this.getContents())
                .createdAt(LocalDateTime.now())
                .member(_member)
                .build();
        return board;
    }

    public boolean isValid(){
        if(this.getTitle().trim().isEmpty() || this.getContents().trim().isEmpty()){
            return false;
        }
        else return true;
    }

    public boolean isFileExisted(){
        return this.getFile().isPresent();
    }

}
