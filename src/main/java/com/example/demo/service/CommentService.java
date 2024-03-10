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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    public List<CommentDto> getCommentList(Long id) throws CustomException {
        Board board = boardRepository.findById(id).orElseThrow(() ->
                new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.BOARD_NOTFOUND));
        return commentRepository.findCommentDtoByBoard(board);
    }

    public CommentDto createComment(CommentCreateForm commentCreateForm, Member member, Board board){
        Comment comment = commentCreateForm.toEntity(member, board);
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
        }
        catch (Exception e){
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.UNKNOWN_ERROR);
        }
    }

    public CommentDto updateComment(Comment comment, CommentCreateForm commentCreateForm){
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
