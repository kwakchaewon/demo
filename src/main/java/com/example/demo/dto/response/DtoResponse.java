package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 값이 Null 일 경우 필드에서 제외
public class DtoResponse<T> {
    private State state;
    private T data;

    @AllArgsConstructor
    @Data
    public static class State{
        private int statusCode;
        private String message;
    }

    public DtoResponse(State state, T data) {
        this.state = state;
        this.data = data;
    }

    public void setSuccess() {
        this.state = new State(200, "success");
    }

    public void setNotBlank() {
        this.state = new State(400, "제목 또는 내용을 빈칸으로 사용할 수 없습니다.");
    }

    public void setBoardNotFound(){
        this.state = new State(404, "게시글을 찾을 수 없습니다.");
    }

    public DtoResponse(T data) {
        this.data = data;
    }
}
