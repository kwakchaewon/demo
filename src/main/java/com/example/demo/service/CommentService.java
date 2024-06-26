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

    public DtoResponse<List<CommentDto>> getCommentList(Long id) {
        boolean isBoardExist = boardRepository.existsById(id);

        // 게시글 부재할 경우 data 없이 404 반환
        if (!isBoardExist) {
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

        // 1. 유저 조회
        Member member = memberRepository.findByUserId(authentication.getName()).
                orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 게시글 조회
        Board board = boardRepository.findById(id).orElse(null);

        // 3. 응답객체 생성
        DtoResponse<Void> dtoResponse = new DtoResponse<>();


        // 4. 해당 게시글이 없을 경우 statusCode 404 리턴
        if (board == null) {
            dtoResponse.setBoardNotFound();
            return dtoResponse;
        }

        // 5. 그 외에 댓글 저장 로직 수행 및 결과값 반환
        Comment comment = new Comment(commentCreateForm.getContents(), member, board);
        commentRepository.save(comment);
        dtoResponse.setSuccess();
        return dtoResponse;
    }

    @Transactional
    public DtoResponse<Void> deleteComment(Long id, Authentication authentication) {
        Comment comment = this.commentRepository.findById(id).orElse(null);
        DtoResponse<Void> dtoResponse = new DtoResponse<>();

        // 존재하지 않는 댓글일 경우 statusCode 404 반환
        if (comment == null) {
            dtoResponse.setCommentNotFound();
            return dtoResponse;
        }

        // 권한 없을 경우
        if (!SecurityUtils.isWriter(authentication, comment.getMember().getUserId())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        // 그 외 댓글 삭제 로직 실행
        try {
            this.commentRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("댓글 삭제에 실패했습니다.");
        }

        dtoResponse.setSuccess();
        return dtoResponse;
    }

    public DtoResponse<CommentDto> updateComment(Long id, CommentCreateForm commentCreateForm, Authentication authentication) {


        Comment comment = this.commentRepository.findById(id).orElse(null);
        DtoResponse<CommentDto> dtoResponse = new DtoResponse<>();

        // 댓글 부재시 statusCode 404 후 리턴
        if (comment == null){
            dtoResponse.setCommentNotFound();
            return dtoResponse;
        }

        // 수정 권한 검증 실패시 403
        if (!SecurityUtils.isWriter(authentication, comment.getMember().getUserId())){
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        // 댓글 업데이트 로직 수행
        comment.update(commentCreateForm);
        commentRepository.save(comment);
        dtoResponse.setData(comment.of());
        dtoResponse.setSuccess();
        return dtoResponse;
    }
}
