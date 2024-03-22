package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 * 페이지네이션과 정렬을 처리 설정 클래스
 * 기본 페이지, 기본 페이지 사이즈 ,최대 페이지 크기 설정
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        SortHandlerMethodArgumentResolver sortArgumentResolver = new SortHandlerMethodArgumentResolver();
        sortArgumentResolver.setSortParameter("sortBy"); // sortBy 요청 파라미터를 통해 정렬정보 설정
        sortArgumentResolver.setPropertyDelimiter("-"); // 정렬 방법 지정시 사용할 문자열 -

        PageableHandlerMethodArgumentResolver pageableArgumentResolver = new PageableHandlerMethodArgumentResolver(sortArgumentResolver);
        pageableArgumentResolver.setOneIndexedParameters(true);
        pageableArgumentResolver.setMaxPageSize(500);  // 최대 페이지 크기
        pageableArgumentResolver.setFallbackPageable(PageRequest.of(0, 10)); // 기본 페이지와 페이지 사이즈 설정
        argumentResolvers.add(pageableArgumentResolver); // 페이지네이션과 관련된 정보를 사용할 수 있도록 사용
    }
}
