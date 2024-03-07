package com.example.demo.service;

import com.example.demo.dto.UploadFileDto;
import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.util.FileStore;
import com.example.demo.util.Pagination;
import com.example.demo.repository.BoardRepository;
import com.example.demo.util.JWTUtil;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    private FileStore fileStore;

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
            BoardDto _dto = boardDto.get();

            if(_dto.getSavedFile()!=null){
                String filePath = _dto.getSavedFile();
                // 1. 이미지 파일이면 imgpath 세팅
                if(fileStore.isImage(filePath)){
                    _dto.setImgPath(filePath);
                }
            }

            return _dto;
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
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public BoardDto createBoard(BoardCreateForm boardCreateForm, Member member) throws IOException {
        if (boardCreateForm.getFile().isPresent()){
            // 파일 저장 및 저장 파일 이름 반환
            String savedFilename = fileStore.savedFile(boardCreateForm.getFile().get());
            Board board = boardCreateForm.toEntityWithFile(member, savedFilename);
            return boardRepository.save(board).of();
        } else{
            Board board = boardCreateForm.toEntity(member);
            return boardRepository.save(board).of();
        }
    }

    public Map<String, Object> getBoardList(Pageable pageable) {

        Map<String, Object> data = new HashMap();
        Page<BoardDto> boardList = boardRepository.findAllBoardDtoByOrderByIdDesc(pageable);

        Pagination pagination = new Pagination(
                (int) boardList.getTotalElements()
                , pageable.getPageNumber() + 1
                , pageable.getPageSize()
                , 10
        );

        data.put("boards", boardList);
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
