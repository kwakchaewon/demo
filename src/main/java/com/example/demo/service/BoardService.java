package com.example.demo.service;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.request.FileRequestForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.util.FileUtils;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    private FileService fileService;

    private FileUtils fileUtils;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

//    public List<BoardDto> findAllBoard(){
//        List<Board> boards =  this.boardRepository.findAll();
//        List<BoardDto> boardDtos = new ArrayList<>();
//
//        for (Board entity : boards) {
//            BoardDto dto = BoardDto.builder()
//                    .id(entity.getId())
//                    .title(entity.getTitle())
//                    .contents(entity.getContents())
//                    .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")))
//                    .build();
//
//            boardDtos.add(dto);
//        }
//        return boardDtos;
//    }

    public BoardDto findBoardById(Long id) throws CustomException {
        Optional<BoardDto> boardDto = boardRepository.findBoardDtoById(id);

        if (boardDto.isPresent()) {
            return boardDto.get();
        } else {
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.BOARD_NOTFOUND);
        }
    }

    public void deleteBoardById(Long id) throws CustomException {
        try {
            this.boardRepository.deleteById(id);
        }
        catch (Exception e){
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.UNKNOWN_ERROR);
        }
    }

    public BoardDto updateBoard(Board board, BoardDto boardDto) throws CustomException {
        try {
            board.updateTitleAndContents(boardDto);
            return boardRepository.save(board).of();
        } catch (Exception e){
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.UNKNOWN_ERROR);
        }
    }

    public ResponseEntity<BoardDto> createBoard(BoardCreateForm boardCreateForm, Member member) throws IOException {
        Board board = boardCreateForm.toEntity(member);
        boardRepository.save(board);

//        List<FileRequestForm> files = fileUtils.uploadFiles(boardCreateForm.getFiles());
//        fileService.saveFiles(board, files);
        BoardDto boardDto = board.of();
        return new ResponseEntity<>(boardDto, HttpStatus.CREATED);
    }

    public Map<String, Object> getBoardList(Pageable pageable) {

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

        return data;
    }

    public Board getBoard(Long id) throws CustomException {

        Optional<Board> _board = this.boardRepository.findById(id);

        if (_board.isPresent()) {
            return _board.get();
        } else {
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.BOARD_NOTFOUND);
        }
    }
}
