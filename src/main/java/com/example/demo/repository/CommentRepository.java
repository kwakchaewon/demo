package com.example.demo.repository;

import com.example.demo.dto.response.CommentDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import org.apache.tomcat.util.http.parser.Cookie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<CommentDto> findCommentDtoByBoard(Board board);
    List<CommentDto> findCommentDtoByBoardIdOrderByCreatedAtAsc(Long id);

    Optional<CommentDto> findCommentDtoById(Long id);
}
