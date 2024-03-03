package com.example.demo.util.exception;

public class Constants {

    /**
     * Exception 상수 통합 관리 클래스
     */

    public enum ExceptionClass{
        OK("Success"),

        // 회원가입 관련
        SIGNUP_USERID_DUPLICATE("이미 존재하는 아이디입니다."),
        SIGNUP_EMAIL_DUPLICATE("이미 존재하는 이메일입니다."),

        // 게시판 관련
        BOARD_NO_AUTHORIZATION("권한이 없습니다."),
        BOARD_ONLY_BLANk("빈 내용은 입력할 수 없습니다."),
        BOARD_NOT_FOUND("게시글을 찾을 수 없습니다."),


        // 댓글 관련
        COMMENT_BOARD_NOTFOUND("해당 게시글이 존재하지 않습니다."),
        COMMENT_NOT_FOUND("해당 댓글이 존재하지 않습니다."),
        COMMENT_NO_AUTHORIZATION("권한이 없습니다."),
        COMMENT_ONLY_BLANk("빈 내용은 입력할 수 없습니다.");

        private String exceptionClass;

        ExceptionClass(String exceptionClass){
            this.exceptionClass =exceptionClass;
        }

        public String getExceptionClass(){
            return exceptionClass;
        }
    }
}
