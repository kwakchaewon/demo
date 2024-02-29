package com.example.demo.controller;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.entity.Member;
import com.example.demo.service.BoardService;
import com.example.demo.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/comment")
@RestController
public class CommentController {
    private BoardService boardService;
    private MemberService memberService;
    @Value("${jwt.secret_access}")
    private String secret_access;

    /**
     * 댓글 작성
     */
//    @PostMapping("")
//    public ResponseEntity<BoardDto> createCommnet(@RequestBody BoardCreateForm boardCreateForm,
//                                                  @RequestHeader("ACCESS_TOKEN") String authorizationHeader){
//        // 빈 내용 유효성 검사
//        if (boardCreateForm.getContents().trim().isEmpty()) {
//            BoardDto boardDto = new BoardDto("빈 내용 입력할 수 없습니다.");
//            return new ResponseEntity<>(boardDto, HttpStatus.BAD_REQUEST);
//        } else {
//            String _userId = boardService.getUserIdByToken(authorizationHeader, secret_access);
//            Member _member = this.memberService.getMemberByUserId(_userId);
//            return this.boardService.createBoard(boardCreateForm, _member);
//        }
//    }

    /**
     * 댓글 전체 보기
     */
//    public ResponseEntity<List<Coment>>

}
