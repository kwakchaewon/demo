package com.example.demo.service;

import com.example.demo.dto.request.CommentReqDto;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;


//    public ResponseEntity<List<Comment>> getCommentList(Long id){
//        Board _board =  boardRepository.getById(id);
//        List<Comment> _commentList = commentRepository.findByBoardId(id);
//        return new ResponseEntity<>(_commentList, HttpStatus.OK);
//    }

    public List<Comment> getCommentList(Long id){
        Board board = boardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("댓글 쓰기 실패: 해당 게시글이 존재하지 않습니다." + id));
        return commentRepository.findCommentsByBoard(board);
    }

    public Long commentSave(String userId, Long id, CommentReqDto dto){
        Member member = memberRepository.findByUserId(userId).get();
        Board board = boardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("댓글 쓰기 실패: 해당 게시글이 존재하지 않습니다." + id));

        dto.setMember(member);
        dto.setBoard(board);

        return dto.getId();
    }
}
