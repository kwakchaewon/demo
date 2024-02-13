package com.example.demo.service;

import com.example.demo.controller.request.CreateAndEditBoardRequest;
import com.example.demo.entity.Board;
import com.example.demo.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> findAllBoard(){
        return this.boardRepository.findAll();
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

    public void updateBoardById(Long id, CreateAndEditBoardRequest request){
        Board board = this.findBoardById(id);
        board.changeBoard(request.getTitle(), request.getContents());
        this.boardRepository.save(board);
    }

    public String createBoard(CreateAndEditBoardRequest request){
        Board board = new Board(request.getTitle(), request.getContents());
        boardRepository.save(board);
        return board.toString();
    }
}
