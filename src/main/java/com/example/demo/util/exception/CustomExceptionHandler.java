package com.example.demo.util.exception;

import com.sun.deploy.net.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
@ControllerAdvice
public class CustomExceptionHandler {

    /**
     * CustomException 처리 메서드
     */
    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<Map<String,String>> handleException(CustomException e){
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> map = new HashMap<>();

        map.put("type", e.getHttpType());
        map.put("code", Integer.toString(e.getHttpCode()));
        map.put("message", e.getExceptionClass().getExceptionClass());

        return new ResponseEntity<>(map, headers, e.getHttpStatus());
    }
}
