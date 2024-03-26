package com.example.demo.service;

import com.example.demo.dto.request.CommentCreateForm;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
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
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final BoardService boardService;

    public List<CommentDto> getCommentList(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));
        return commentRepository.findCommentDtoByBoard(board);
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

    public CommentDto updateComment(Comment comment, CommentCreateForm commentCreateForm) {
        comment.update(commentCreateForm);
        commentRepository.save(comment);
        return new CommentDto(comment);
    }

    public CommentDto findCommentById(Long id) throws CustomException {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.COMMENT_NOTFOUND));
        CommentDto commentDto = new CommentDto(comment);
        return commentDto;
    }
}
