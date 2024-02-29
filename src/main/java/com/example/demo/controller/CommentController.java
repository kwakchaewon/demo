package com.example.demo.controller;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.request.CommentReqDto;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @PostMapping("/board/{id}/comments")
    public ResponseEntity createCommnet(
            @PathVariable Long id,
            @RequestBody CommentReqDto commentReqDto,
            @RequestHeader("ACCESS_TOKEN") String authorizationHeader){

        String _userId = boardService.getUserIdByToken(authorizationHeader, secret_access);
        Member _member = this.memberService.getMemberByUserId(_userId);

        // 빈 내용 유효성 검사
        if (commentReqDto.getContents().trim().isEmpty()) {
            return new ResponseEntity<>("빈 내용 입력할 수 없습니다.", HttpStatus.BAD_REQUEST);
        } else {
            commentService.commentSave(_member.getUserId(), id, commentReqDto);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * 댓글 전체 보기
     */
//    @GetMapping("/{id}")
//    public ResponseEntity<List<Comment>> commentList(@PathVariable("id") Long id){
//        return commentService.getCommentList(id);
//    }

}
