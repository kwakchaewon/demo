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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        List<CommentDto> comments = commentRepository.findCommentDtoByBoardIdOrderByCreatedAtAsc(id);
        DtoResponse<List<CommentDto>> dtoResponse = new DtoResponse<>(comments);
        dtoResponse.setSuccess();

        return dtoResponse;
    }

    @Transactional
    public DtoResponse<Void> createComment(Long id, CommentCreateForm commentCreateForm, Authentication authentication) {
//        Member member = memberService.getMemberByUserId(authentication.getName());
        
        // 1. 유저 조회
        Member member = memberRepository.findByUserId(authentication.getName()).
                orElseThrow(()-> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        
        // 2. 게시글 조회
        Board board = boardRepository.findById(id).orElse(null);
        
        // 3. 응답객체 생성
        DtoResponse<Void> dtoResponse = new DtoResponse<>();

        
        // 4. 해당 게시글이 없을 경우 statusCode 404 리턴
        if (board == null){
            dtoResponse.setBoardNotFound();
            return dtoResponse;
        }

        // 5. 그 외에 댓글 저장 로직 수행 및 결과값 반환
        Comment comment = new Comment(commentCreateForm.getContents(), member, board);
        commentRepository.save(comment);
        dtoResponse.setSuccess();
        return dtoResponse;
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
