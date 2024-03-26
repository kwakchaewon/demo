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
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    @Autowired
    private FileStore fileStore;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public BoardDto findBoardById(Long id) {
        return boardRepository.findBoardDtoById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));
    }

    @Transactional
    public void deleteBoardById(Long id) throws CustomException {

        // 1. 게시글 조회 실패시 NOT_FOUND 반환
        Board board = boardRepository.findById(id).
                orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.BOARD_NOTFOUND));

        // 2. 파일 조회 실패시 NOT_FOUND 반환
        if (board.getSavedFile() != null) {
            fileStore.deleteFile(board.getSavedFile());
        }

        try {
            this.boardRepository.delete(board);
        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public BoardDto updateBoard(Board board, BoardUpdateForm boardUpdateForm) throws CustomException, IOException {

        // 1. 원본 파일이 변경되지않았다면
        // 제목 & 내용의 변경만을 저장
        if (!boardUpdateForm.isIsupdate()) {
            board.updateTitleAndContents(boardUpdateForm);
            return boardRepository.save(board).of();
        }

        // 2. 원본 파일 변경시
        else {
            fileStore.deleteFile(board.getSavedFile()); // 1. 디렉토리 내 게시글 파일 제거

            // 2.1 원본 파일이 삭제됐다면 :
            // 원본 파일 삭제 및 DB 필드 null로 변경,  제목 & 내용 변경만을 저장
            if (!boardUpdateForm.getFile().isPresent()) {
                board.setSavedFile(null);
                board.setOriginalFile(null);
            }

            // 2.2 원본 파일이 변경됐을 경우:
            // 제목 & 내용 변경 저장
            // 원본 파일 삭제 후 파일 저장
            else {
                String savedFilename = fileStore.savedFile(boardUpdateForm.getFile()); // UUID 파일명
                board.setOriginalFile(boardUpdateForm.getFile().get().getOriginalFilename());
                board.setSavedFile(savedFilename);
            }

            board.updateTitleAndContents(boardUpdateForm); // 게시글과 내용 변경
            return boardRepository.save(board).of();
        }
    }

    @Transactional
    public BoardDto createBoard(BoardCreateForm boardCreateForm, Member member) throws IOException {
        if (boardCreateForm.isFileExisted()) {
            // 1. 파일 존재시 경로에 파일 저장
            String savedFilename = fileStore.savedFile(Optional.of(boardCreateForm.getFile().get())); // UUID 파일명
            Board board = boardCreateForm.toEntityWithFile(member, savedFilename);
            return boardRepository.save(board).of();

        } else {
            // 2. 파일 부재 시 게시글 저장
            Board board = boardCreateForm.toEntity(member);
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

    public Board getBoard(Long id) throws CustomException {

        Optional<Board> _board = this.boardRepository.findById(id);

        if (_board.isPresent()) {
            return _board.get();
        } else {
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.BOARD_NOTFOUND);
        }
    }

    public Resource getImage(BoardDto boardDto) throws CustomException, IOException {
        String strPath = fileStore.getFullPath(boardDto.getSavedFile());

        if (fileStore.isImage(strPath)) {
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

    public Resource getDownloadResource(BoardDto boardDto) throws CustomException {
        String savedFileName = boardDto.getSavedFile();
        Path filePath = Paths.get(fileStore.getFullPath(savedFileName));

        try {
            Resource resource = new InputStreamResource(Files.newInputStream(filePath));
            return resource;
        } catch (IOException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ExceptionClass.FILE_IOFAILED);
        }
    }

//    public Specification<Board> search(String keyword) {
//        return new Specification<Board>() {
//            private static final long serialVersionUID = 1L;
//            @Override
//            public Predicate toPredicate(Root<Board> b, CriteriaQuery<?> query, CriteriaBuilder cb) {
//                query.distinct(true);  // 중복 제거
//                Join<Board, Member> u1 = b.join("member", JoinType.LEFT);
//                Join<Board, Comment> a = b.join("comments", JoinType.LEFT);
//                Join<Comment, Member> u2 = a.join("member", JoinType.LEFT);
//                return cb.or(cb.like(b.get("title"), "%" + keyword + "%"), // 제목
//                        cb.like(b.get("contents"), "%" + keyword + "%"),      // 내용
//                        cb.like(u1.get("userId"), "%" + keyword + "%"),    // 게시글 작성자
//                        cb.like(a.get("contents"), "%" + keyword + "%"),      // 답변 내용
//                        cb.like(u2.get("userId"), "%" + keyword + "%"));   // 답변 작성자
//            }
//        };
//    }

//    public Page<BoardDto> getList(int page, String kwargs){
//        List<Sort.Order> sorts = new ArrayList<>();
//        sorts.add(Sort.Order.desc("createdAt"));
//        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
//        Specification<Board> spec = search(kwargs);
//        return this.boardRepository.findAllBoardDtoBy(spec, pageable);
//    }
}
