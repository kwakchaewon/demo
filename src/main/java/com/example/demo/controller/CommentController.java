package com.example.demo.controller;
import com.example.demo.dto.request.CommentReqDto;
import com.example.demo.entity.Member;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.MemberService;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/comment")
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final BoardService boardService;
    private final MemberService memberService;

    @Value("${jwt.secret_access}")
    private String secret_access;

    /**
     * 댓글 작성
     */
    @PostMapping("")
    public ResponseEntity createCommnet(
            @PathVariable Long id,
            @RequestBody CommentReqDto commentReqDto,
            @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {

        String _userId = boardService.getUserIdByToken(authorizationHeader, secret_access);
        Member _member = this.memberService.getMemberByUserId(_userId);

        // 빈 내용 유효성 검사
        if (commentReqDto.getContents().trim().isEmpty()) {
            throw  new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.BOARD_ONLY_BLANk);
        } else {
            commentService.commentSave(_member.getUserId(), id, commentReqDto);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * 댓글 삭제
     */
//    @DeleteMapping("/{id}")
//    public ResponseEntity deleteComment(@PathVariable("id") Long id,
//                                        @RequestHeader("Access_TOKEN") String authorizationHeader) throws CustomException {
//
//
//
//    }

}
