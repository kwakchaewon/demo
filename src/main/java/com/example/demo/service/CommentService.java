package com.example.demo.service;

import com.example.demo.dto.request.CommentCreateForm;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.dto.response.DtoResponse;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.util.SecurityUtils;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    // 이 부분 사용 지양할것. 순환참조 발생할 수 있음.
    private final MemberService memberService;
    private final BoardService boardService;

    public DtoResponse<List<CommentDto>> getCommentList(Long id) {
        boolean isBoardExist = boardRepository.existsById(id);

        // 게시글 부재할 경우 data 없이 404 반환
        if (!isBoardExist){
            DtoResponse dtoResponse = new DtoResponse<>();
            dtoResponse.setBoardNotFound();
            return dtoResponse;
        }

        List<CommentDto> comments = commentRepository.findCommentDtoByBoardIdOrderByCreatedAtDesc(id);
        DtoResponse<List<CommentDto>> dtoResponse = new DtoResponse<>(comments);
        dtoResponse.setSuccess();

        return dtoResponse;
    }

    public CommentDto createComment(Long id, CommentCreateForm commentCreateForm, Authentication authentication) {
        Member member = memberService.getMemberByUserId(authentication.getName());
        Board board = boardService.getBoard(id);
        Comment comment = new Comment(commentCreateForm.getContents(), member, board);
        return commentRepository.save(comment).of();
    }

    public Comment getComment(Long id) throws CustomException {

        Optional<Comment> _comment = this.commentRepository.findById(id);

        if (_comment.isPresent()) {
            return _comment.get();
        } else {
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.COMMENT_NOTFOUND);
        }
    }

    public void deleteCommentById(Long id) throws CustomException {
        try {
            this.commentRepository.deleteById(id);
        } catch (NullPointerException e) {
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.COMMENT_NOTFOUND);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.UNKNOWN_ERROR);
        }
    }

    public CommentDto updateComment(Long id, CommentCreateForm commentCreateForm, Authentication authentication) throws CustomException {

        Comment comment = this.getComment(id);

        if (SecurityUtils.isWriter(authentication, comment.getMember().getUserId())){
            comment.update(commentCreateForm);
            return commentRepository.save(comment).of();
        }

        else {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
    }

    public CommentDto findCommentById(Long id) throws CustomException {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.COMMENT_NOTFOUND));
        return new CommentDto(comment);
    }


}
