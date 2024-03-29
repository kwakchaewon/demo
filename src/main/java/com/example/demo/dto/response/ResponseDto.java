package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 값이 Null 일 경우 필드에서 제외
public class ResponseDto<T> {
    private State state;
    private T data;

    @AllArgsConstructor
    @Data
    public static class State{
        private int statusCode;
        private String message;
    }

    public ResponseDto(State state, T data) {
        this.state = state;
        this.data = data;
    }

    public ResponseDto(T data) {
        this.state.setStatusCode(200);
        this.state.setMessage("success");
        this.data = data;

    }

    public ResponseDto(State state) {
        this.state = state;
        this.data = null;
    }


}
