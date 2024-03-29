package com.example.demo.dto.response;
import com.example.demo.util.Pagination;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

/**
 * 페이지네이션 응답 객체
 * @param <T>
 */

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // 값이 Null 일 경우 필드에서 제외
public class PagingResponse<T> {
    private State state;
    private Pagination pagination;
    private Page<T> list;


    @Data
    @AllArgsConstructor
    public static class State{
        private int statusCode;
        private String message;
    }

    public PagingResponse(Page<T> list, Pagination pagination) {
        this.list = list;
        this.pagination = pagination;
    }

}
