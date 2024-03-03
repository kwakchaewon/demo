package com.example.demo.controller;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.request.CommentCreateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.MemberService;
import com.example.demo.util.JWTUtil;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/board")
@RestController
@Validated
@RequiredArgsConstructor
public class BoardController {

    @Value("${jwt.secret_access}")
    private String secret_access;

    @Autowired
    private BoardService boardService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    JWTUtil jwtUtil;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     * 게시글 작성
     */
    @PostMapping("")
    public ResponseEntity<BoardDto> createBoardDone(@RequestBody BoardCreateForm boardCreateForm,
                                                    @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {

        // 빈 제목, 내용 유효성 검사
        if (boardCreateForm.getTitle().trim().isEmpty() || boardCreateForm.getContents().trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.BOARD_ONLY_BLANk);
        } else {
            String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);
            Member _member = this.memberService.getMemberByUserId(_userId);
            return this.boardService.createBoard(boardCreateForm, _member);
        }
    }

    /**
     * 게시글 상세
     */
    @GetMapping("/{id}")
    public BoardDto detailBoard(@PathVariable("id") Long id) {
        BoardDto boardDto = boardService.findBoardById(id);
        return boardDto;
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteBoard(@PathVariable("id") Long id,
                                      @RequestHeader("Access_TOKEN") String authorizationHeader) throws CustomException {
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);
        Board board = this.boardService.getBoard(id);

        if (!board.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.BOARD_NO_AUTHORIZATION);
        } else {
            return boardService.deleteBoardById(id);
        }
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoardDto> updateBoard(@PathVariable("id") Long id,
                                                @RequestBody BoardDto boardDto,
                                                @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {

        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);
        Board board = boardService.getBoard(id);

        if (!board.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.BOARD_NO_AUTHORIZATION);
        } else {
            return boardService.updateBoard(board, boardDto);
        }
    }

    /**
     * 페이징 기반 게시판 목록
     */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> pagingBoardList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable
    ) {
        return boardService.getBoardList(pageable);
    }

    /**
     * 게시글 수정 권한 검증
     */
    @GetMapping("/{id}/check")
    public ResponseEntity checkUpdateAuth(@PathVariable("id") Long id,
                                          @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);
        Board board = this.boardService.getBoard(id);

        if (!board.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.BOARD_NO_AUTHORIZATION);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * 상세 게시판 댓글 조회
     */
    @GetMapping("/{id}/comment")
    public ResponseEntity commentList(@PathVariable("id") Long id) throws CustomException {
        List<CommentDto> commentDtoList = commentService.getCommentList(id);
        return new ResponseEntity(commentDtoList, HttpStatus.OK);
    }

    /**
     * 댓글 작성
     */
    @PostMapping("/{id}/comment")
    public ResponseEntity createComment(@PathVariable("id") Long id,
                                        @RequestBody CommentCreateForm commentCreateForm,
                                        @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {
        // 1. 작성자 가져오기
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);
        Member member = memberService.getMemberByUserId(_userId);

        // 2. 게시글 가져오기
        Board board = this.boardService.getBoard(id);

        // 빈 내용 유효성 검사
        if (commentCreateForm.getContents().trim().isEmpty()) {
            return new ResponseEntity<>("빈 내용 입력할 수 없습니다.", HttpStatus.BAD_REQUEST);
        } else {
            return commentService.createComment(commentCreateForm, member, board);
        }
    }
}
