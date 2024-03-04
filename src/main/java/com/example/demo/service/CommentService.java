package com.example.demo.service;

import com.example.demo.dto.request.CommentCreateForm;
import com.example.demo.dto.request.CommentReqDto;
import com.example.demo.dto.response.BoardDto;
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
import org.springframework.http.ResponseEntity;
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

        // 찾는 Board 가 없다면 404 반환
        Board board = boardRepository.findById(id).orElseThrow(() ->
                new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.BOARD_NOTFOUND));
        List<Comment> commentList = commentRepository.findByBoard(board);

        return commentList.stream().map(CommentDto::new).collect(Collectors.toList());
    }

    public Long commentSave(String userId, Long id, CommentReqDto dto){
        Member member = memberRepository.findByUserId(userId).get();
        Board board = boardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("댓글 쓰기 실패: 해당 게시글이 존재하지 않습니다." + id));

        dto.setMember(member);
        dto.setBoard(board);

        return dto.getId();
    }

    public ResponseEntity createComment(CommentCreateForm commentCreateForm, Member member, Board board){
        Comment comment = commentCreateForm.toEntity(member, board);
        commentRepository.save(comment);
        CommentDto commentDto = comment.of();
        return new ResponseEntity<>(commentDto,HttpStatus.CREATED);
    }

    public Comment getComment(Long id) throws CustomException {

        Optional<Comment> _comment = this.commentRepository.findById(id);

        if (_comment.isPresent()) {
            return _comment.get();
        } else {
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.COMMENT_NOTFOUND);
        }
    }

    public ResponseEntity deleteCommentById(Long id){
        this.commentRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<CommentDto> updateComment(Comment comment, CommentCreateForm commentCreateForm){
        comment.update(commentCreateForm);
        commentRepository.save(comment);
        CommentDto commentDto = new CommentDto(comment);
        return new ResponseEntity<>(commentDto ,HttpStatus.OK);
    }

    public CommentDto findCommentById(Long id) throws CustomException {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.COMMENT_NOTFOUND));
        CommentDto commentDto = new CommentDto(comment);
        return commentDto;
    }
}
