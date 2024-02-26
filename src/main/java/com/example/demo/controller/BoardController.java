package com.example.demo.controller;

import com.example.demo.dto.BoardCreateForm;
import com.example.demo.dto.BoardDto;
import com.example.demo.dto.BoardResponse;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.model.Header;
import com.example.demo.service.BoardService;
import com.example.demo.service.MemberService;
import com.example.demo.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequestMapping("/board")
@RestController
@Validated
public class BoardController {

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
//    @PostMapping("/write")
//    public Board createBoardDone(@RequestBody @Valid BoardDto boardDto,
//                                 @RequestHeader("Authorization") String authorizationHeader){
//
//        String token = authorizationHeader.substring(7);
//        String _userId = jwtUtil.decodeToken(token).getClaim("userId").asString();
//        Member _member = this.memberService.getMemberByUserId(_userId);
//        return this.boardService.createBoard(boardDto, _member);
//    }

//    @PostMapping("/write")
//    public ResponseEntity<?> createBoardDone(@Valid @RequestBody BoardCreateForm boardCreateForm,
//                                                  @RequestHeader("Authorization") String authorizationHeader){
//
//        // title, contents 빈칸에 대한 유효성 검사
//        if (boardCreateForm.getTitle() == null || boardCreateForm.getTitle().isEmpty()){
//            return ResponseEntity.badRequest().body("게시글 제목은 필수입니다.");
//        }
//        else if(boardCreateForm.getContents() == null || boardCreateForm.getContents().isEmpty()){
//            return ResponseEntity.badRequest().body("게시글 내용은 필수입니다.");
//        } else {
//            String token = authorizationHeader.substring(7);
//            String _userId = jwtUtil.decodeToken(token).getClaim("userId").asString();
//            Member _member = this.memberService.getMemberByUserId(_userId);
//            Board newBoard = this.boardService.createBoard(boardCreateForm, _member);
//            return ResponseEntity.ok(Long.toString(newBoard.getId()));
//        }
//    }

    @PostMapping("/write")
    public BoardResponse createBoardDone(@RequestBody BoardCreateForm boardCreateForm,
                                         @RequestHeader("ACCESS_TOKEN") String authorizationHeader){

        String _userId = getUserIdByToken(authorizationHeader);
        Member _member = this.memberService.getMemberByUserId(_userId);
        BoardResponse boardResponse = this.boardService.createBoard(boardCreateForm, _member);

        return boardResponse;
    }

    /**
     * 게시글 상세
     */
    @GetMapping("/{id}")
    public BoardDto detailBoard(@PathVariable("id") Long id) throws Exception {
        return boardService.findBoardById(id);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable("id") Long id,
                            @RequestHeader("Access_TOKEN") String authorizationHeader){
        String _userId = getUserIdByToken(authorizationHeader);

        Board board = this.boardService.getBoard(id);

        if (!board.getAuthor().getUserId().equals(_userId)) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }

        boardService.deleteBoardById(id);
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{id}")
    public Board updateBoard(@PathVariable("id") Long id,
                             @RequestBody BoardDto boardDto,
                             @RequestHeader("Access_TOKEN") String authorizationHeader){
        String _userId = getUserIdByToken(authorizationHeader);

        Board board = this.boardService.getBoard(id);

        if (!board.getAuthor().getUserId().equals(_userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        return boardService.updateBoardById(id, boardDto);
    }

    /**
     *  페이징 기반 게시판 목록
     */
    @GetMapping("/list")
    public Header<List<BoardDto>> pagingBoardList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable
    ) {
        return boardService.getBoardList(pageable);
    }

    /**
     *  게시글 수정 권한 검증
     */
    @GetMapping("/{id}/updatecheck")
    public ResponseEntity<String> checkUpdateAuth(@PathVariable("id") Long id,
                                                  @RequestHeader("Access_TOKEN") String authorizationHeader
    ){
        String _userId = getUserIdByToken(authorizationHeader);

        Board board = this.boardService.getBoard(id);

        if (!board.getAuthor().getUserId().equals(_userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }else{
            return ResponseEntity.ok("수정 창으로 이동합니다.");
        }
    }

    /**
     * 토큰 decode 후 userId 리턴
     */
    private String getUserIdByToken(String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String _userId = jwtUtil.decodeToken(token).getClaim("userId").asString();
        return _userId;
    }
}
