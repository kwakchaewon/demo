package com.example.demo.controller;

import com.example.demo.controller.request.CreateAndEditBoardRequest;
import com.example.demo.dto.BoardDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RequestMapping("/board")
@RestController
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


//    /**
//     *  게시판 목록
//     */
//    @GetMapping("/list")
//    public List<BoardDto> getBoards(){
//        return boardService.findAllBoard();
//    }

    /**
     * 게시글 작성폼
     */
    @GetMapping("/write")
    public String createBoard(){
        return "createBoard API";
    }

    /**
     * 게시글 작성
     */
    @PostMapping("/write")
    public Board createBoardDone(@RequestBody BoardDto boardDto,
                                 @RequestHeader("Authorization") String authorizationHeader){

        String token = authorizationHeader.substring(7);
        String _userId = jwtUtil.decodeToken(token).getClaim("userId").asString();
        Member _member = this.memberService.getMemberByUserId(_userId);
        return this.boardService.createBoard(boardDto, _member);
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
    public void deleteBoard(@PathVariable("id") Long id){
        boardService.deleteBoardById(id);
    }

    /**
     * 게시글 수정 폼
     */
    @GetMapping("/update/{id}")
    public String updateBoardForm(@PathVariable("id") Integer id){
        return "updateBoardForm API";
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{id}")
    public Board updateBoard(@PathVariable("id") Long id,
                             @RequestBody BoardDto boardDto,
                             @RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        String _userId = jwtUtil.decodeToken(token).getClaim("userId").asString();

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
}
