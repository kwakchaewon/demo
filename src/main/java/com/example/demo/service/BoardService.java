package com.example.demo.service;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.request.BoardUpdateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.dto.response.PagingResponse;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.util.FileStore;
import com.example.demo.util.Pagination;
import com.example.demo.repository.BoardRepository;
import com.example.demo.util.SecurityUtils;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberService memberService;

    public BoardService(BoardRepository boardRepository, MemberService memberService) {
        this.boardRepository = boardRepository;
        this.memberService = memberService;
    }

    public BoardDto findBoardById(Long id) {
        return boardRepository.findBoardDtoById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));
    }

    @Transactional
    public void deleteBoardById(BoardDto boardDto) {
        // 2. 파일 삭제 로직 실패시 NOT_FOUND 반환
        if (boardDto.getSavedFile() != null) {
            FileStore.deleteFile(boardDto.getSavedFile());
        }

        // 3. 게시글 삭제
        try {
            this.boardRepository.deleteById(boardDto.getId());
        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new RuntimeException("에러가 발생 했습니다: " + e);
        }
    }

    @Transactional
    public BoardDto updateBoard(Long id, BoardUpdateForm boardUpdateForm, Authentication authentication) throws IOException {

        // 1. 게시글 추출
        Board board = this.getBoard(id);

        // 2. 수정 권한 검증
        if (!SecurityUtils.isWriter(authentication, board.of())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        // 3. 검증 성공시 파일단 수정
        else {
            // 원본 파일이 변경됐다면 파일 변경 로직 실행
            if (boardUpdateForm.isIsupdate()) {
                FileStore.deleteFile(board.getSavedFile()); // 1. 디렉토리 내 게시글 파일 제거

                // 파일 존재시 원본 파일 삭제 후 파일 새로 저장
                if (boardUpdateForm.existFile()) {
                    String savedFilename = FileStore.savedFile(boardUpdateForm.getFile()); // UUID 파일명 디렉토리 저장
                    board.updateFile(boardUpdateForm.getOriginalFile(), savedFilename); // 첨부파일명 DB 업데이트
                }

                // 파일이 삭제시 원본 파일 삭제 및 DB 필드 null로 변경
                else {
                    board.resetFile();
                }
            }

            // 4. 제목 + 내용 update
            board.updateTitleAndContents(boardUpdateForm);
            return boardRepository.save(board).of();
        }
    }

    @Transactional
    public BoardDto createBoard(BoardCreateForm boardCreateForm, String _userId) throws IOException {

        // 1. Member 추출
        Member _member = this.memberService.getMemberByUserId(_userId);

        // 2. 파일 존재시 파일 저장 로직 수행
        if (boardCreateForm.isFileExisted()) {
            String savedFilename = FileStore.savedFile(Optional.of(boardCreateForm.getFile().get())); // UUID 파일명
            Board board = boardCreateForm.toEntityWithFile(_member, savedFilename);
            return boardRepository.save(board).of();

        // 3. 파일 부재시 게시글 저장 로직 수행
        } else {
            Board board = boardCreateForm.toEntity(_member);
            return boardRepository.save(board).of();
        }
    }

    public PagingResponse<BoardDto> getBoardList(Pageable pageable, String keyword) {

        Page<BoardDto> boards = boardRepository.findBoardDtoByTitleContainingOrderByIdDesc(keyword, pageable);

        Pagination pagination = new Pagination(
                (int) boards.getTotalElements()
                , pageable.getPageNumber() + 1
                , pageable.getPageSize()
                , 10
        );

        return new PagingResponse<>(boards, pagination);
    }

    public Board getBoard(Long id) {

        Board _board = this.boardRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));
        return _board;
    }

    public Resource getImage(BoardDto boardDto) throws IOException {
        String strPath = FileStore.getFullPath(boardDto.getSavedFile());

        if (FileStore.isImage(strPath)) {
            Path filePath = Paths.get(strPath);

            // 2.1 이미지 파일일 경우, 이미지 파일 추출시도 (실패시, 500 반환)
            try {
                return new InputStreamResource(Files.newInputStream(filePath));
            } catch (IOException e) {
                System.out.println("error = " + e);
                throw new IOException("이미지 파일 추출에 실패했습니다.");
            }

            // 2.1 이미지 파일이 아닐 경우 404 반환
        } else {
            throw new FileNotFoundException("이미지 파일이 존재하지 않습니다.");
        }
    }

    public Resource extractResource(BoardDto boardDto) throws IOException {
        
        // dto 로부터 uuid 기반 파일명, 파일 경로 추출
        String savedFileName = boardDto.getSavedFile();
        Path filePath = Paths.get(FileStore.getFullPath(savedFileName));

        // 로컬로부터 파일 추출
        try {
            return new InputStreamResource(Files.newInputStream(filePath));
        } catch (IOException e) {
            throw new IOException("첨부파일 다운로드에 실패했습니다.");
        }
    }
}
