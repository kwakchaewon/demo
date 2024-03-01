package com.example.demo.util.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomException extends Exception{
    /**
     * Http 상태 코드와 exceptionClass 초기화
     */
    
    private HttpStatus httpStatus;
    private Constants.ExceptionClass exceptionClass;

    public CustomException(HttpStatus httpStatus, Constants.ExceptionClass exceptionClass){
        this.httpStatus =httpStatus;
        this.exceptionClass = exceptionClass;
    }

    public int getHttpCode(){
        return httpStatus.value();
    }

    public String getHttpType(){
        return httpStatus.getReasonPhrase();
    }
}
