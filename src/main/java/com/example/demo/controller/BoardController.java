package com.example.demo.controller;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.request.BoardUpdateForm;
import com.example.demo.dto.request.CommentCreateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.dto.response.PagingResponse;
import com.example.demo.dto.response.DtoResponse;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.util.FileStore;
import com.example.demo.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 게시글 관련 컨트롤러
 * 게시판 CRUD 담당
 * 사용자 조회, 사용자 삭제, 사용자 권한 변경
 * security context를 통한 권한 정보 접근
 */
@RequestMapping("/board")
@RestController
@RequiredArgsConstructor
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private CommentService commentService;

    /**
     * 게시글 등록(완료)
     *
     * @param boardCreateForm 게시글 저장 폼
     * @param authentication  인증 객체
     * @return DtoResponse 상태 + 게시글 정보
     * @throws IOException
     */
    @PostMapping(value = "")
    public DtoResponse<BoardDto> createBoardDone(@ModelAttribute BoardCreateForm boardCreateForm,
                                                 Authentication authentication) throws IOException {

        // 1. 유효성 검사 통과시 게시글 저장 로직 실행
        if (boardCreateForm.isValid()) {
            String _userId = authentication.getName();

            // 게시글 저장 및 BoardDto 추출
            return this.boardService.createBoard(boardCreateForm, _userId);
        }

        // 2. 유효성 검사 예외처리
        else {
            DtoResponse<BoardDto> dtoResponse = new DtoResponse<>();
            dtoResponse.setNotBlank();
            return dtoResponse;
//            throw new IllegalArgumentException("제목 또는 내용을 비워둘 수 없습니다.");
        }
    }

    /**
     * 페이징 기반 게시판 목록 (완료)
     *
     * @param pageable 페이징 객체
     * @param keyword  검색 키워드
     * @return PagingResponse: 상태 + 게시글 정보
     */
    @GetMapping("")
    public PagingResponse<BoardDto> pagingBoardList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable,
            String keyword
    ) {
        return boardService.getBoardList(pageable, keyword);
    }

    /**
     * 게시글 상세 (완료)
     *
     * @param id 게시글 id
     * @return ResponseDto 상태 + 게시글 상세 정보
     */
    @GetMapping("/{id}")
    public DtoResponse<BoardDto> detailBoard(@PathVariable("id") Long id) {
        // 상세 게시글 추출
        return boardService.getBoardDtoRes(id);
    }

    /**
     * 게시글 상세 이미지 (완료)
     *
     * @param id 게시글 id
     * @return 이미지 리소스
     * @throws IOException
     */
    @GetMapping("/{id}/image")
    public Resource getBoardImage(@PathVariable("id") Long id) throws IOException {

        // 1. Board 추출 (실패시, 204 반환)
        BoardDto boardDto = boardService.findBoardById(id);

        // 2. 파일이 존재한다면 이미지 추출 (실패시 204, 500 반환)
        if (boardDto.getSavedFile() != null) {
            return boardService.getImage(boardDto);
        }

        // 3. 파일이 존재하지 않을 경우 null 반환
        else {
            return null;
        }
    }

    /**
     * 게시글 수정 (완료)
     *
     * @param id              게시글 id
     * @param boardUpdateForm 게시글 수정폼
     * @param authentication  인증객체
     * @return 상태 + 게시글 상세 정보
     * @throws IOException
     */
    @PutMapping("/{id}")
    public DtoResponse<Void> updateBoard(@PathVariable("id") Long id,
                                         @ModelAttribute BoardUpdateForm boardUpdateForm,
                                         Authentication authentication) throws IOException {

        return boardService.updateBoard(id, boardUpdateForm, authentication);
    }

    /**
     * 게시글 수정 권한 검증 및 상세 반환 (완료)
     *
     * @param id             게시글 id
     * @param authentication 인증 객체
     * @return 상태 + 게시글 상세 정보
     */
    @GetMapping("/{id}/check")
    public DtoResponse<BoardDto> checkUpdateAuth(@PathVariable("id") Long id,
                                                 Authentication authentication) {

        // 1. Board 추출 (게시글 부재시 404 반환)
        DtoResponse<BoardDto> dtoResponse = this.boardService.getBoardDtoRes(id);
        BoardDto boardDto = dtoResponse.getData();

        if (boardDto != null) {

            boolean hasUpdatePermission = SecurityUtils.isWriter(authentication, dtoResponse.getData().getMemberId());

            // 2. 수정 권한 검증 실패시 403 반환
            if (!hasUpdatePermission) {
                throw new AccessDeniedException("수정 권한이 없습니다.");
            }
        }

        return dtoResponse;
    }

    /**
     * 게시글 삭제 (완료)
     *
     * @param id             게시글 id
     * @param authentication 인증 객체
     * @return 상태
     */
    @DeleteMapping("/{id}")
    public DtoResponse deleteBoard(@PathVariable("id") Long id, Authentication authentication) {
        return boardService.deleteBoard(id, authentication);
    }

    // DtoResponse<List<CommentDto>> 새로 DTO 만들어야 할 것 같다.

    /**
     * 게시글 댓글 리스트 조회 (완료)
     *
     * @param id 게시글 id
     * @return 상태 + 게시글 정보
     */
    @GetMapping("/{id}/comment")
    public DtoResponse<List<CommentDto>> commentList(@PathVariable("id") Long id) {
        // 댓글 조회 로직
        return commentService.getCommentList(id);
    }

    /**
     * 댓글 작성 (완료)
     *
     * @param id 게시글 id
     * @param commentCreateForm 댓글 작성 폼
     * @param authentication 인증 객체
     * @return 상태
     */
    @PostMapping("/{id}/comment")
    public DtoResponse<Void> createComment(@PathVariable("id") Long id,
                                    @RequestBody CommentCreateForm commentCreateForm,
                                    Authentication authentication) {
        // 1. 내용 빈칸 유효성 검사 실패시
        if (!commentCreateForm.isValid()){
            DtoResponse<Void> dtoResponse = new DtoResponse<>();
            dtoResponse.setNotBlank();
            return dtoResponse;
        }

        // 2. 성공시 댓글 작성 로직 수행
        return commentService.createComment(id, commentCreateForm, authentication);
    }

    /**
     * 파일 다운로드
     *
     * @param id: 게시글 id
     * @return ResponseEntity<Resource> 파일리소스
     * @throws ResponseStatusException:      파일 부재
     * @throws IOException:                  파일 입출력
     * @throws UnsupportedEncodingException: 파일 인코딩 실패
     */
    @GetMapping("/{id}/file")
    @CrossOrigin(value = {"*"}, exposedHeaders = {"Content-Disposition"})
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {

        // 1. boardDto 추출 (실패시, 404 반환)
        BoardDto boardDto = boardService.findBoardById(id);

        // 2. Resouce 추출
        Resource resource = boardService.extractResource(boardDto);

        // 3. 파일명 UTF-8로 인코딩 설정 및 contentDisposition 추출
        String contentDisposition = FileStore.getContentDisposition(boardDto.getOriginalFile());

        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition) // Content-Disposition: 브라우저에게 응답으로 리소스가 다운로드 되어야 함을 명시
                .body(resource);
    }
}