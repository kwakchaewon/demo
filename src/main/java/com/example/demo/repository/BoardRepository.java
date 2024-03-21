package com.example.demo.repository;

import com.example.demo.dto.response.BoardDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAllByOrderByIdDesc(Pageable pageable);
//    Page<BoardDto> findAllBoardDtoByOrderByIdDesc(Pageable pageable);
//    Page<BoardDto> findByTitleContainingBoardDtoBy(String keyword, Pageable pageable);
    Page<BoardDto> findBoardDtoByTitleContainingOrderByIdDesc(String keyword, Pageable pageable);
    Optional<BoardDto>  findBoardDtoById(Long id);
}
