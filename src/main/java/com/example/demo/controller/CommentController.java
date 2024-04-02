package com.example.demo.controller;

import com.example.demo.dto.request.CommentCreateForm;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.dto.response.DtoResponse;
import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import com.example.demo.service.CommentService;
import com.example.demo.util.SecurityUtils;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 댓글 관련 컨트롤러
 * 댓글 UD 담당
 * 상세 댓글 조회, 댓글 수정, 댓글 수정
 */
@RequestMapping("/comment")
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final CommentRepository commentRepository;

    /**
     * 댓글 삭제 (완료)
     * 
     * @param id 아이디
     * @param authentication 인증 객체
     * @return 상태
     */
    @DeleteMapping("/{id}")
    public DtoResponse<Void> deleteComment(@PathVariable("id") Long id, Authentication authentication)  {
        return commentService.deleteComment(id, authentication);
    }

    /**
     * 댓글 수정
     *
     * @param id:                댓글 id
     * @param commentCreateForm: 댓글 수정 폼
     * @param authentication:    인증 객체
     * @return 댓글 정보
     * @throws CustomException
     */
    @PutMapping("/{id}")
    public CommentDto updateComment(@PathVariable("id") Long id,
                                    @RequestBody CommentCreateForm commentCreateForm,
                                    Authentication authentication) throws CustomException {
        return commentService.updateComment(id, commentCreateForm, authentication);
    }

    /**
     * 댓글 수정 권한 검증
     *
     * @param id: 댓글 id
     * @param authentication: 인증 객체
     * @throws CustomException
     */
    @GetMapping("/{id}/check")
    public void checkCommentUpdateAuth(@PathVariable("id") Long id, Authentication authentication) throws CustomException {
        // 1. CommentDto 추출 (실패시, 404 반환)
        CommentDto commentDto = this.commentService.findCommentById(id);

        // 2. 수정 권한 검증 (실패시, 403 반환)
        if (!SecurityUtils.isWriter(authentication, commentDto.getMemberId())) {
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
