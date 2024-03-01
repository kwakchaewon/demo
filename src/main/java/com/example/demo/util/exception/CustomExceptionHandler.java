package com.example.demo.util.exception;

import com.sun.deploy.net.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CustomExceptionHandler {
    
    /**
     * CustomException 처리 메서드
     */
    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<Map<String,String>> handleException(CustomException e, HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> map = new HashMap<>();

        map.put("type", e.getHttpType());
        map.put("code", Integer.toString(e.getHttpCode()));
        map.put("message", e.getMessage());

        return new ResponseEntity<>(map, headers, e.getHttpStatus());
    }
}
