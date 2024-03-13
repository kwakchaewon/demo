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
    public ResponseEntity deleteComment(@PathVariable("id") Long id,
                                        @RequestHeader("Access_TOKEN") String authorizationHeader) throws CustomException {

        // 1. userId 추출
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);
        
        // 2. Comment 추출 (실패시, 404 반환)
        Comment comment = this.commentService.getComment(id);

        // 3. 삭제 권한 검증 (실패시 403 반환)
        if (!comment.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, Constants.ExceptionClass.NO_AUTHORIZATION);
        } else {
            // 4. 댓글 삭제 성공시 200 반환 (실패시 400 반환)
            commentService.deleteCommentById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("id") Long id,
                                                    @RequestBody CommentCreateForm commentCreateForm,
                                                    @RequestHeader("Access_TOKEN") String authorizationHeader) throws CustomException {

        // 1. userId 추출
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);

        // 2. Comment 추출 (실패시, 404 반환)
        Comment comment = this.commentService.getComment(id);

        // 3. 수정 권한 검증 (실패시 403 반환)
        if (!comment.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, Constants.ExceptionClass.NO_AUTHORIZATION);
        } else {
            // 4. 댓글 수정 성공시 200 반환
            CommentDto commentDto = commentService.updateComment(comment, commentCreateForm);
            return new ResponseEntity<>(commentDto, HttpStatus.OK);
        }
    }

    /**
     * 댓글 수정 권한 검증
     */
    @GetMapping("/{id}/check")
    public ResponseEntity checkCommentUpdateAuth(@PathVariable("id") Long id,
                                          @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {
        // 1. userId 추출
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);

        // 2. Comment 추출 (실패시, 404 반환)
        Comment comment = this.commentService.getComment(id);

        // 3. 수정 권한 검증 (실패시 403 반환)
        if (!comment.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, Constants.ExceptionClass.NO_AUTHORIZATION);
        } else {
            // 4. 권한 검증 성공시 200 반환
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * 상세 댓글 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> detailBoard(@PathVariable("id") Long id) throws CustomException {
        // 1. id기반 댓글 찾기 (실패시, 404 반환)
        CommentDto commentDto = commentService.findCommentById(id);
        // 2. 성공시 200 반환
        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }
}
