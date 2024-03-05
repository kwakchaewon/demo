package com.example.demo.controller;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.request.CommentCreateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.FileService;
import com.example.demo.service.MemberService;
import com.example.demo.util.FileUtils;
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

import java.io.IOException;
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
    JWTUtil jwtUtil;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileUtils fileUtils;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     * 게시글 작성
     */
    @PostMapping("")
    public ResponseEntity<BoardDto> createBoardDone(@RequestBody BoardCreateForm boardCreateForm,
                                                    @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException, IOException {

        // 빈 제목, 내용 유효성 검사
        if (boardCreateForm.getTitle().trim().isEmpty() || boardCreateForm.getContents().trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.ONLY_BLANk);
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
    public ResponseEntity<BoardDto> detailBoard(@PathVariable("id") Long id) throws CustomException {
        BoardDto boardDto = boardService.findBoardById(id);
        return new ResponseEntity<>(boardDto, HttpStatus.OK);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteBoard(@PathVariable("id") Long id,
                                      @RequestHeader("Access_TOKEN") String authorizationHeader) throws CustomException {

        // 1. userId 추출
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);

        // 2. Board 추출 (실패시 404 반환)
        Board board = this.boardService.getBoard(id);

        // 3. 삭제 권한 검증 (실패시 403 반환)
        if (!board.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, Constants.ExceptionClass.NO_AUTHORIZATION);
        } else {
            // 4. 게시글 삭제 및 200 반환
            boardService.deleteBoardById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoardDto> updateBoard(@PathVariable("id") Long id,
                                                @RequestBody BoardDto boardDto,
                                                @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {
        // 1. userId 추출
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);

        // 2. Board 추출 (실패시 404 반환)
        Board board = boardService.getBoard(id);

        // 3. 수정 권한 검증 (실패시 403 반환)
        if (!board.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, Constants.ExceptionClass.NO_AUTHORIZATION);
        } else {
            // 4. 게시글 수정 및 200 반환
            BoardDto updatedBoard = boardService.updateBoard(board, boardDto);
            return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
        }
    }

    /**
     * 페이징 기반 게시판 목록
     */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> pagingBoardList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable
    ) {
        Map<String, Object> data = boardService.getBoardList(pageable);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /**
     * 게시글 수정 권한 검증
     */
    @GetMapping("/{id}/check")
    public ResponseEntity checkUpdateAuth(@PathVariable("id") Long id,
                                          @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {
        // 1. userId 추출
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);

        // 2. Board 추출 (실패시 404 반환)
        Board board = this.boardService.getBoard(id);

        if (!board.getMember().getUserId().equals(_userId)) {
            // 3. 수정 권한 검증 (실패시 403 반환)
            throw new CustomException(HttpStatus.FORBIDDEN, Constants.ExceptionClass.NO_AUTHORIZATION);
        } else {
            // 4. 검증 성공시 200 반환
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * 상세 게시판 댓글 조회
     */
    @GetMapping("/{id}/comment")
    public ResponseEntity<List<CommentDto>> commentList(@PathVariable("id") Long id) throws CustomException {
        // 1. boardId 로 해당 게시글 댓글 조회 (실패시 404 반환)
        List<CommentDto> commentDtoList = commentService.getCommentList(id);
        
        // 2. 댓글 조회 성공시 200 반환
        return new ResponseEntity<>(commentDtoList, HttpStatus.OK);
    }

    /**
     * 댓글 작성
     */
    @PostMapping("/{id}/comment")
    public ResponseEntity<CommentDto> createComment(@PathVariable("id") Long id,
                                                 @RequestBody CommentCreateForm commentCreateForm,
                                                 @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {
        // 1. userId 추출
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);

        // 2. Member 추출 (실패시 401 반환)
        Member member = memberService.getMemberByUserId(_userId);

        // 3. Board 추출 (실패시 404 반환)
        Board board = this.boardService.getBoard(id);

        // 4. 빈 내용 유효성 검사 (실패시 400 반환)
        if (commentCreateForm.getContents().trim().isEmpty()) {
            throw  new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.ONLY_BLANk);
        } else {
            // 5. 댓글 작성 성공시 200 반환
            CommentDto commentDto = commentService.createComment(commentCreateForm, member, board);
            return new ResponseEntity<>(commentDto, HttpStatus.OK);
        }
    }
}