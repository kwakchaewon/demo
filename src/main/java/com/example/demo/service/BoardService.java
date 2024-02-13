package com.example.demo.service;

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

    public Optional<Board> findBoardById(Long id){
        return this.boardRepository.findById(id);
    }

    public void deleteBoardById(Long id){
        this.boardRepository.deleteById(id);
    }
}
