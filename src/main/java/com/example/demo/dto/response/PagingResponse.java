package com.example.demo.dto.response;
import com.example.demo.util.Pagination;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Data
public class PagingResponse<T> {

    private Page<T> list;
    private Pagination pagination;

    public PagingResponse(Page<T> list, Pagination pagination) {
        this.list = list;
        this.pagination = pagination;
    }
}
