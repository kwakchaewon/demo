package com.example.demo.util.exception;

public class Constants {

    /**
     * Exception 상수 통합 관리 클래스
     */

    public enum ExceptionClass{
        OK("Success"),

        // 회원가입 관련
        USERID_DUPLICATED("이미 존재하는 아이디입니다."), // 400
        EMAIL_DUPLICATED("이미 존재하는 이메일입니다."), // 400
        MEMBER_NOTFOUND("존재하지 않는 회원입니다."), // 404

        // 권한 관련
        NO_AUTHORIZATION("권한이 없습니다."), // 403
        USER_INVALID("유효하지 않은 사용자입니다."), // 401

        // 유효성 검사
        ONLY_BLANk("빈 내용은 입력할 수 없습니다."), //400

        // 자원 관련
        BOARD_NOTFOUND("게시글을 찾을 수 없습니다."), //404
        COMMENT_NOTFOUND("댓글을 찾을 수 없습니다."), // 404
        FILE_NOTFOUND("파일을 찾을 수 없습니다"), // 404
        FILE_IOFAILED("파일 입출력에 실패했습니다."), // 500
        IMAGE_NOTFOUND("이미지를 찾을 수 없습니다"), //404
        UNKNOWN_ERROR("알 수 없는 오류가 발생했습니다."), // 400
        MULTIFILE_EXCEPTION("MULTIFILE_EXCEPTION"), // 400
        LOGINSESSION_EXPIRED("로그인 세션이 만료됐습니다.");  // 401

        private String exceptionClass;

        ExceptionClass(String exceptionClass){
            this.exceptionClass =exceptionClass;
        }

        public String getExceptionClass(){
            return exceptionClass;
        }
    }
}
