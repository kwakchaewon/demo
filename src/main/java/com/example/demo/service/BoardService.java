package com.example.demo.service;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.util.Pagination;
import com.example.demo.repository.BoardRepository;
import com.example.demo.util.JWTUtil;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    @Autowired
    JWTUtil jwtUtil;

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
        BoardDto boardDto = new BoardDto(board);
        return boardDto;
    }

    public ResponseEntity deleteBoardById(Long id){
        this.boardRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<BoardDto> updateBoard(Board board, BoardDto boardDto){
        board.update(boardDto);
        boardRepository.save(board);
        boardDto.updateIdAndAuthor(board);
        return new ResponseEntity<>(boardDto, HttpStatus.OK);
    }

    public ResponseEntity<BoardDto> createBoard(BoardCreateForm boardCreateForm, Member member){
        Board board = boardCreateForm.toEntity(member);
        boardRepository.save(board);
        BoardDto boardDto = board.of();
        return new ResponseEntity<>(boardDto, HttpStatus.CREATED);
    }

    public ResponseEntity<Map<String, Object>> getBoardList(Pageable pageable) {

        Map<String, Object> data = new HashMap();
        Page<Board> boardList = boardRepository.findAllByOrderByIdDesc(pageable);

        List<BoardDto> boardDtoList =  boardList.stream().map(BoardDto::new).collect(Collectors.toList());

        Pagination pagination = new Pagination(
                (int) boardList.getTotalElements()
                , pageable.getPageNumber() + 1
                , pageable.getPageSize()
                , 10
        );

        data.put("boards", boardDtoList);
        data.put("pagination", pagination);

        return new ResponseEntity<>(data ,HttpStatus.OK);
    }

    public Board getBoard(Long id) throws CustomException {
        Optional<Board> _board = this.boardRepository.findById(id);

        if (_board.isPresent()) {
            return _board.get();
        } else {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.BOARD_NOT_FOUND);
        }
    }

    public String getUserIdByToken(String authorizationHeader, String secret) {
        String token = authorizationHeader.substring(7);
        return jwtUtil.decodeToken(token, secret).getClaim("userId").asString();
    }
}
