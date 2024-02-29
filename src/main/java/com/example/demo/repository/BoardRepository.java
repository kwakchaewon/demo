package com.example.demo.repository;

import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAllByOrderByIdDesc(Pageable pageable);
}
