package com.example.demo.service;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.util.Pagination;
import com.example.demo.repository.BoardRepository;
import com.example.demo.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public ResponseEntity<BoardDto> findBoardById(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        BoardDto boardDto =  BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .contents(board.getContents())
                .createdAt(board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
                .author(board.getAuthor())
                .build();

        return new ResponseEntity<>(boardDto, HttpStatus.OK);
    }

    public ResponseEntity deleteBoardById(Long id){
        this.boardRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<BoardDto> updateBoard(Board board, BoardDto boardDto){
        board.changeBoard(boardDto.getTitle(), boardDto.getContents());
        boardRepository.save(board);
        boardDto.setId(board.getId());
        boardDto.setCreatedAt(board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        boardDto.setAuthor(board.getAuthor());
        return new ResponseEntity<>(boardDto, HttpStatus.OK);
    }

//        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
//        board.setTitle(boardDto.getTitle());
//        board.setContents(boardDto.getContents());
//        return new ResponseEntity<>(,)


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

    public ResponseEntity<Map<String, Object>> getBoardList(Pageable pageable) {

        Map<String, Object> data = new HashMap();
        List<BoardDto> boards = new ArrayList<>();

        Page<Board> boardEntities = boardRepository.findAllByOrderByIdDesc(pageable);
        // Board jpa 로  native query로 만들어야 됐다.
        
        for (Board entity : boardEntities) {
            BoardDto dto = BoardDto.builder()
                    .id(entity.getId())
                    .title(entity.getTitle())
                    .contents(entity.getContents())
                    .author(entity.getAuthor())
                    .createdAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")))
                    .build();

            boards.add(dto);
        }

        Pagination pagination = new Pagination(
                (int) boardEntities.getTotalElements()
                , pageable.getPageNumber() + 1
                , pageable.getPageSize()
                , 10
        );

        data.put("boards", boards);
        data.put("pagination", pagination);

        return new ResponseEntity<>(data ,HttpStatus.OK);
        // 컨트롤러 단에서 담기 + 예외 처리
    }

    public Board getBoard(Long id){
        Optional<Board> board = this.boardRepository.findById(id);
        return board.get();
    }

    public String getUserIdByToken(String authorizationHeader, String secret) {
        String token = authorizationHeader.substring(7);
        return jwtUtil.decodeToken(token, secret).getClaim("userId").asString();
    }
}
