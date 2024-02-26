package com.example.demo.service;

import com.example.demo.dto.BoardCreateForm;
import com.example.demo.dto.BoardDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.model.Header;
import com.example.demo.model.Pagination;
import com.example.demo.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<BoardDto> findAllBoard(){
        List<Board> boards =  this.boardRepository.findAll();
        List<BoardDto> boardDtos = new ArrayList<>();

        for (Board entity : boards) {
            BoardDto dto = BoardDto.builder()
                    .id(entity.getId())
                    .title(entity.getTitle())
                    .contents(entity.getContents())
                    .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")))
                    .build();

            boardDtos.add(dto);
        }

        return boardDtos;
    }

    public BoardDto findBoardById(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        BoardDto boardDto =  BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .contents(board.getContents())
                .createdAt(board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
                .author(board.getAuthor())
                .build();

        return boardDto;
    }

    public void deleteBoardById(Long id){
        this.boardRepository.deleteById(id);
    }

    public Board updateBoardById(Long id, BoardDto boardDto){
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        board.setTitle(boardDto.getTitle());
        board.setContents(boardDto.getContents());
        return this.boardRepository.save(board);
    }

//    public Board createBoard(BoardCreateForm boardCreateForm, Member _author){
//        Board board = Board.builder()
//                .title(boardCreateForm.getTitle())
//                .contents(boardCreateForm.getContents())
//                .createdAt(LocalDateTime.now())
//                .author(_author)
//                .build();
//        return boardRepository.save(board);
//    }

    public ResponseEntity<BoardDto> createBoard(BoardCreateForm boardCreateForm, Member _author){

        Board board = Board.builder()
                .title(boardCreateForm.getTitle())
                .contents(boardCreateForm.getContents())
                .createdAt(LocalDateTime.now())
                .author(_author)
                .build();

        Long id = boardRepository.save(board).getId();

        BoardDto boardDto = BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .contents(board.getContents())
                .createdAt(board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")))
                .author(board.getAuthor())
                .build();

        return new ResponseEntity<>(boardDto, HttpStatus.CREATED);
    }

    public Header<List<BoardDto>> getBoardList(Pageable pageable) {
        List<BoardDto> dtos = new ArrayList<>();

        Page<Board> boardEntities = boardRepository.findAllByOrderByIdDesc(pageable);
        for (Board entity : boardEntities) {
            BoardDto dto = BoardDto.builder()
                    .id(entity.getId())
                    .title(entity.getTitle())
                    .contents(entity.getContents())
                    .author(entity.getAuthor())
                    .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")))
                    .build();

            dtos.add(dto);
        }

        Pagination pagination = new Pagination(
                (int) boardEntities.getTotalElements()
                , pageable.getPageNumber() + 1
                , pageable.getPageSize()
                , 10
        );

        return Header.OK(dtos, pagination);
    }

    public Board getBoard(Long id){
        Optional<Board> board = this.boardRepository.findById(id);
        return board.get();
    }

}
