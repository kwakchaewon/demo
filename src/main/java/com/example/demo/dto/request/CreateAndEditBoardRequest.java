package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAndEditBoardRequest {
    private String title;
    private String contents;

    @Override
    public String toString() {
        return "CreateAndEditBoardRequest{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }
}
