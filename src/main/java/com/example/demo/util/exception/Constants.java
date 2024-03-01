package com.example.demo.util.exception;

public class Constants {

    /**
     * Exception 상수 통합 관리 클래스
     */

    public enum ExceptionClass{
        OK("Success"),

        // 회원가입 관련
        SIGNUP_USERID_DUPLICATE("이미 존재하는 아이디입니다."),
        SIGNUP_EMAIL_DUPLICATE("이미 존재하는 이메일입니다.");

        private String exceptionClass;

        ExceptionClass(String exceptionClass){
            this.exceptionClass =exceptionClass;
        }

        public String getExceptionClass(){
            return exceptionClass;
        }
    }
}
