package com.example.demo.controller;

import com.example.demo.dto.request.CommentCreateForm;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.entity.Comment;
import com.example.demo.service.CommentService;
import com.example.demo.util.JWTUtil;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/comment")
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Value("${jwt.secret_access}")
    private String secret_access;

    @Autowired
    private final JWTUtil jwtUtil;

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable("id") Long id) throws CustomException {

        // 1. Comment 추출 (실패시, 404 반환)
        Comment comment = this.commentService.getComment(id);

        // 2. 권한 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String _userId = authentication.getName();
        String auth = authentication.getAuthorities().stream().findFirst().get().getAuthority();

        // 3. 권한 검증: 작성자 or SUPERVISOR
        if (_userId.equals(comment.getMember().getUserId()) || auth.equals("ROLE_SUPERVISOR")) {
            commentService.deleteCommentById(id);
        } else {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
    }

    /**
     * 댓글 수정
     * 댓글 부재시 COMMENT_NOTFOUND(404)
     * 권한 검증 실패시 403
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("id") Long id,
                                                    @RequestBody CommentCreateForm commentCreateForm) throws CustomException {
        // 1. _userid 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String _userId = authentication.getName();

        // 2. Comment 추출 (실패시, 404 반환)
        Comment comment = this.commentService.getComment(id);

        // 3. 수정 권한 검증 (실패시, 403 반환)
        if (_userId.equals(comment.getMember().getUserId())) {
            CommentDto commentDto = commentService.updateComment(comment, commentCreateForm);
            return new ResponseEntity<>(commentDto, HttpStatus.OK);
        } else {
            throw new AccessDeniedException("수정 권한이 없습니다."); // 403
        }
    }

    /**
     * 댓글 수정 권한 검증
     * 권한 검증 실패시 403
     */
    @GetMapping("/{id}/check")
    public ResponseEntity checkCommentUpdateAuth(@PathVariable("id") Long id) throws CustomException {
        // 1. _userid 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String _userId = authentication.getName();

        // 2. Comment 추출 (실패시, 404 반환)
        Comment comment = this.commentService.getComment(id);

        // 3. 수정 권한 검증 (실패시, 403 반환)
        if (_userId.equals(comment.getMember().getUserId())) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new AccessDeniedException("수정 권한이 없습니다."); // 403
        }
    }

    /**
     * 상세 댓글 조회
     * 권한 검증 실패시 403
     * 댓글 부재시 COMMENT_NOTFOUND (404)
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> detailBoard(@PathVariable("id") Long id) throws CustomException {

        // 1. _userid 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String _userId = authentication.getName();

        // 2. id기반 댓글 찾기 (실패시, 404 반환)
        CommentDto commentDto = commentService.findCommentById(id);

        if (_userId.equals(commentDto.getMemberId())) {
            return new ResponseEntity<>(commentDto, HttpStatus.OK);
        } else {
            throw new AccessDeniedException("수정 권한이 없습니다."); // 403
        }
    }
}
