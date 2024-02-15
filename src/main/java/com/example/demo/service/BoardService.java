package com.example.demo.service;

import com.example.demo.controller.request.CreateAndEditBoardRequest;
import com.example.demo.dto.BoardDto;
import com.example.demo.entity.Board;
import com.example.demo.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
//                    .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
                    .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")))
                    .build();

            boardDtos.add(dto);
        }

        return boardDtos;
    }

    public Board findBoardById(Long id) {
        Optional <Board> board = this.boardRepository.findById(id);
        return board.get();

//        if (board.isPresent()){
//            return board.get();
//        } else {
//            throw new Exception("Data Not Found");
//        }
    }

    public void deleteBoardById(Long id){
        this.boardRepository.deleteById(id);
    }

    public Board updateBoardById(Long id, CreateAndEditBoardRequest request){
        Board board = this.findBoardById(id);
        board.changeBoard(request.getTitle(), request.getContents());
        return this.boardRepository.save(board);
    }

    public Board createBoard(CreateAndEditBoardRequest request){
        Board board = new Board(request.getTitle(), request.getContents());
        return boardRepository.save(board);
    }
}
