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
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public BoardDto findBoardById(Long id) throws CustomException {
        Optional<BoardDto> boardDto = boardRepository.findBoardDtoById(id);

        if (boardDto.isPresent()) {
            BoardDto _dto = boardDto.get();

            if (_dto.getSavedFile() != null) {
                String filePath = fileStore.getFullPath(_dto.getSavedFile());
                // 1. 이미지 파일이면 imgpath 세팅
                if (fileStore.isImage(filePath)) {
                    _dto.setImgPath(filePath);
                }
            }

            return _dto;
        } else {
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.BOARD_NOTFOUND);
        }
    }

    public void deleteBoardById(Long id) throws CustomException {
        Board board = boardRepository.findById(id).
                orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.BOARD_NOTFOUND));

        if (board.getSavedFile() != null) {
            fileStore.deleteFile(board.getSavedFile());
        }

        this.boardRepository.delete(board);
    }

    public BoardDto updateBoard(Board board, BoardDto boardDto) throws CustomException {
        try {
            board.updateTitleAndContents(boardDto);
            return boardRepository.save(board).of();
        } catch (Exception e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public BoardDto createBoard(BoardCreateForm boardCreateForm, Member member) throws CustomException {
        if (boardCreateForm.getFile().isPresent()) {
            // 1. 파일 존재시 경로에 파일 저장
            try {
                String savedFilename = fileStore.savedFile(boardCreateForm.getFile().get()); // UUID 파일명
                Board board = boardCreateForm.toEntityWithFile(member, savedFilename);  // UUID 파일명, Member 정보로 게시글 Entity 생성
                return boardRepository.save(board).of();  // 게시글 저장
            }
            // 파일 저장시 발생하는 IOException 처리
            catch (IOException e){
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ExceptionClass.FILE_IOFAILED);
            }

        } else {
            // 2. 파일 부재 시 게시글 저장
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

    public Resource getImage(BoardDto boardDto) throws CustomException {
        String strPath = fileStore.getFullPath(boardDto.getSavedFile());

        if (fileStore.isImage(strPath)) {
            Path filePath = Paths.get(strPath);

            // 2.1 이미지 파일일 경우, 이미지 파일 추출시도 (실패시, 500 반환)
            try {
                Resource resource = new InputStreamResource(Files.newInputStream(filePath));
                return resource;
            } catch (IOException e) {
                System.out.println("error = " + e);
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ExceptionClass.FILE_IOFAILED);
            }

            // 2.1 이미지 파일이 아닐 경우 404 반환
        } else {
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.IMAGE_NOTFOUND);
        }
    }

    public Resource getDownloadResource(BoardDto boardDto) throws CustomException {
        String savedFileName = boardDto.getSavedFile();
        Path filePath = Paths.get(fileStore.getFullPath(savedFileName));

        try {
            Resource resource = new InputStreamResource(Files.newInputStream(filePath));
            return resource;
        }catch (IOException e){
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ExceptionClass.FILE_IOFAILED);
        }
    }


}
