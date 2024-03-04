package com.example.demo.controller;
import com.example.demo.dto.request.CommentCreateForm;
import com.example.demo.dto.request.CommentReqDto;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.MemberService;
import com.example.demo.util.JWTUtil;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final JWTUtil jwtUtil;

    /**
     * 댓글 작성
     */
    @PostMapping("")
    public ResponseEntity createCommnet(
            @PathVariable Long id,
            @RequestBody CommentReqDto commentReqDto,
            @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {

        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);
        Member _member = this.memberService.getMemberByUserId(_userId);

        // 빈 내용 유효성 검사
        if (commentReqDto.getContents().trim().isEmpty()) {
            throw  new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.ONLY_BLANk);
        } else {
            commentService.commentSave(_member.getUserId(), id, commentReqDto);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteComment(@PathVariable("id") Long id,
                                        @RequestHeader("Access_TOKEN") String authorizationHeader) throws CustomException {

        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);
        Comment comment = this.commentService.getComment(id);
//        Board board = this.boardService.getBoard(id);

        if (!comment.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, Constants.ExceptionClass.NO_AUTHORIZATION);
        } else {
            return commentService.deleteCommentById(id);
        }
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("id") Long id,
                                                    @RequestBody CommentCreateForm commentCreateForm,
                                                    @RequestHeader("Access_TOKEN") String authorizationHeader) throws CustomException {

        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);
        Comment comment = this.commentService.getComment(id);

        if (!comment.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, Constants.ExceptionClass.NO_AUTHORIZATION);
        } else {
            return commentService.updateComment(comment, commentCreateForm);
        }
    }

    /**
     * 댓글 수정 권한 검증
     */
    @GetMapping("/{id}/check")
    public ResponseEntity checkCommentUpdateAuth(@PathVariable("id") Long id,
                                          @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);
        Comment comment = this.commentService.getComment(id);

        if (!comment.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, Constants.ExceptionClass.NO_AUTHORIZATION);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * 상세 댓글 조회
     */
    @GetMapping("/{id}")
    public CommentDto detailBoard(@PathVariable("id") Long id) throws CustomException {
        CommentDto commentDto = commentService.findCommentById(id);
        return commentDto;
    }
}
