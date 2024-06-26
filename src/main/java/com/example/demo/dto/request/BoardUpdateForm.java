package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardUpdateForm {
    private String title;
    private String contents;
    private Optional<MultipartFile> file = Optional.empty();
    private boolean isupdate;

    public boolean existFile() {
        if (this.getFile().isPresent()) return true;
        else return false;
    }

    public String getOriginalFile() {
        return this.getFile().get().getOriginalFilename();
    }
}
