package com.example.demo.dto.response;
import com.example.demo.util.Pagination;
import lombok.Data;
import org.springframework.data.domain.Page;

/**
 * 페이지네이션 응답 객체
 * @param <T>
 */

@Data
public class PagingResponse<T> {

    private Page<T> list;
    private Pagination pagination;

    public PagingResponse(Page<T> list, Pagination pagination) {
        this.list = list;
        this.pagination = pagination;
    }
}
