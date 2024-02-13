package com.example.demo.controller;

import com.example.demo.entity.Board;
import com.example.demo.repository.BoardRepository;
import com.example.demo.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/board")
@RestController
//@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }


    /**
     *  게시판 목록
     */
    @GetMapping("/list")
    public List<Board> getBoards(){
        return boardService.findAllBoard();
    }

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
    public String createBoardDone(){
        return "createBoardDone API";
    }

    /**
     * 게시글 상세
     */
    @GetMapping("/{id}")
    public String detailBoard(@PathVariable("id") Integer id){
        return "detailBoard API";
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{id}")
    public String deleteBoard(@PathVariable("id") Integer id){
        return "deleteBoard API";
    }

    /**
     * 게시글 수정 폼
     */
    @GetMapping("update/{id}")
    public String updateBoardForm(@PathVariable("id") Integer id){
        return "updateBoardForm API";
    }

    /**
     * 게시글 수정
     */
    @PostMapping("update/{id}")
    public String updateBoard(@PathVariable("id") Integer id){
        return "updateBoardForm API";
    }
}
