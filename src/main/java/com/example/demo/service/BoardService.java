package com.example.demo.service;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.request.BoardUpdateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.dto.response.PagingResponse;
import com.example.demo.dto.response.DtoResponse;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.util.FileStore;
import com.example.demo.util.Pagination;
import com.example.demo.repository.BoardRepository;
import com.example.demo.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;

    public BoardDto findBoardById(Long id) {
        return boardRepository.findBoardDtoById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "게시글이 존재하지 않습니다."));
    }

    // 게시글 상세 조회
    public DtoResponse<BoardDto> getBoardDtoRes(Long id) {
        BoardDto boardDto = boardRepository.findBoardDtoById(id).orElse(null);

        // 성공
        if (boardDto != null) {
            DtoResponse.State state = new DtoResponse.State(200, "success");
            return new DtoResponse<>(state, boardDto);
        }

        // 실패: 게시글 부재
        else {
            DtoResponse<BoardDto> dtoResponse = new DtoResponse<>();
            dtoResponse.setBoardNotFount();
            return dtoResponse;
        }
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
        if (!SecurityUtils.isWriter(authentication, board.of().getMemberId())) {
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
    public DtoResponse<BoardDto> createBoard(BoardCreateForm boardCreateForm, String _userId) throws IOException {

        // 1. Member 추출
        Member _member = this.getMemberByUserId(_userId);

        // 2. 파일 존재 여부 확인
        boolean fileExists = boardCreateForm.isFileExisted();

        // 3. 파일 저장 로직 수행
        String savedFilename = fileExists ? FileStore.savedFile(Optional.of(boardCreateForm.getFile().get())) : null;

        // 4. 게시글 저장
        Board board = fileExists ? boardCreateForm.toEntityWithFile(_member, savedFilename) : boardCreateForm.toEntity(_member);
        BoardDto boardDto = boardRepository.save(board).of();

        // 5. DtoResponse 설정
        DtoResponse<BoardDto> dtoResponse = new DtoResponse<>(boardDto);
        dtoResponse.setSuccess();

        return dtoResponse;
    }

    public PagingResponse<BoardDto> getBoardList(Pageable pageable, String keyword) {

        // 1. Page<BoardDto>
        Page<BoardDto> boards = boardRepository.findBoardDtoByTitleContainingOrderByIdDesc(keyword, pageable);

        // 2. pagination
        Pagination pagination = new Pagination(
                (int) boards.getTotalElements()
                , pageable.getPageNumber() + 1
                , pageable.getPageSize()
                , 10
        );

        // 3. PagingResponse 선언
        PagingResponse<BoardDto> pagingResponse = new PagingResponse<>(boards, pagination);
        pagingResponse.setSuccess();

        return pagingResponse;

//        PagingResponse.State state = new PagingResponse.State(200, "success");
//        return new PagingResponse<>(state, pagination, boards);
    }

    public Board getBoard(Long id) {

        Board _board = this.boardRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));
        return _board;
    }

    public Resource getImage(BoardDto boardDto) throws IOException {
        String strPath = FileStore.getFullPath(boardDto.getSavedFile());

        // 이미지 파일일 경우
        if (FileStore.isImage(strPath)) {
            Path filePath = Paths.get(strPath);

            // 이미지 파일 추출 시도
            try {
                return new InputStreamResource(Files.newInputStream(filePath));
            }

            // 실패시 500 반환
            catch (IOException e) {
                throw new IOException("이미지 파일 추출에 실패했습니다.");
            }
        }

        // 이미지 파일이 아닐 경우 null 반환
        else {
            return null;
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

    public Member getMemberByUserId(String userId) {
        return this.memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
