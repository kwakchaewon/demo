package com.example.demo.controller;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.service.BoardService;
import com.example.demo.service.MemberService;
import com.example.demo.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RequestMapping("/board")
@RestController
@Validated
public class BoardController {

    @Value("${jwt.secret_access}")
    private String secret_access;

    @Autowired
    private BoardService boardService;

    @Autowired
    private MemberService memberService;

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
                                         @RequestHeader("ACCESS_TOKEN") String authorizationHeader){

        // 빈 제목 유효성 검사
        if (boardCreateForm.getTitle().trim().isEmpty()){
            BoardDto boardDto = new BoardDto("빈 제목은 입력할 수 없습니다.");
            return new ResponseEntity<>(boardDto, HttpStatus.BAD_REQUEST);
        }
        // 빈 내용 유효성 검사
        else if (boardCreateForm.getContents().trim().isEmpty()) {
            BoardDto boardDto = new BoardDto("빈 내용 입력할 수 없습니다.");
            return new ResponseEntity<>(boardDto, HttpStatus.BAD_REQUEST);
        }
        else {
            String _userId = boardService.getUserIdByToken(authorizationHeader, secret_access);
            Member _member = this.memberService.getMemberByUserId(_userId);
            return this.boardService.createBoard(boardCreateForm, _member);
        }
    }

    /**
     * 게시글 상세
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> detailBoard(@PathVariable("id") Long id) throws Exception {
        return boardService.findBoardById(id);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteBoard(@PathVariable("id") Long id,
                            @RequestHeader("Access_TOKEN") String authorizationHeader){
        String _userId =boardService.getUserIdByToken(authorizationHeader, secret_access);
        Board board = this.boardService.getBoard(id);

        if (!board.getMember().getUserId().equals(_userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }

        return boardService.deleteBoardById(id);
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoardDto> updateBoard(@PathVariable("id") Long id,
                             @RequestBody BoardDto boardDto,
                             @RequestHeader("ACCESS_TOKEN") String authorizationHeader){
        String _userId = boardService.getUserIdByToken(authorizationHeader, secret_access);
        Board board = boardService.getBoard(id);

        if (!board.getMember().getUserId().equals(_userId)) {
            boardDto.setErrMsg("수정권한이 없습니다.");
            return new ResponseEntity<>(boardDto ,HttpStatus.BAD_REQUEST);
        } else {
            return boardService.updateBoard(board, boardDto);
        }
    }

    /**
     *  페이징 기반 게시판 목록
     */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> pagingBoardList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable
    ) {
        return boardService.getBoardList(pageable);
    }

    /**
     *  게시글 수정 권한 검증
     */
    @GetMapping("/{id}/check")
    public ResponseEntity<String> checkUpdateAuth(@PathVariable("id") Long id,
                                                  @RequestHeader("ACCESS_TOKEN") String authorizationHeader
    ){
        String _userId = boardService.getUserIdByToken(authorizationHeader, secret_access);
        Board board = this.boardService.getBoard(id);

        if (!board.getMember().getUserId().equals(_userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }else{
            return ResponseEntity.ok("수정 창으로 이동합니다.");
        }
    }
}
