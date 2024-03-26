package com.example.demo.controller;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.request.BoardUpdateForm;
import com.example.demo.dto.request.CommentCreateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.dto.response.PagingResponse;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.MemberService;
import com.example.demo.util.JWTUtil;
import com.example.demo.util.SecurityUtils;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private MemberService memberService;
    @Autowired
    private CommentService commentService;

    /**
     * 페이징 기반 게시판 목록
     */
    @GetMapping("")
    public PagingResponse<BoardDto> pagingBoardList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable,
            String keyword
    ) {
        return boardService.getBoardList(pageable, keyword);
    }

    /**
     * 게시글 등록
     *
     * @param boardCreateForm: 게시글 등록 폼
     * @param authentication: 인증 정보
     * @return BoardDto: 등록 게시글 정보
     * @throws IOException:파일입출력 예외, IllegalArgumentException: 빈칸 유효성 검사, UsernameNotFoundException: 사용자 인증 실패
     */
    @PostMapping(value = "")
    public BoardDto createBoardDone(@ModelAttribute BoardCreateForm boardCreateForm,
                                    Authentication authentication) throws IOException {

        // 1. 유효성 검사 (빈 제목, 빈 내용)
        if (!boardCreateForm.isValid()) {
            throw new IllegalArgumentException("제목 또는 내용을 비워둘 수 없습니다.");
        }

        // 2. 유효성 검사 통과시
        else {
            String _userId = authentication.getName();
            Member _member = this.memberService.getMemberByUserId(_userId);

            // 3. 게시글 저장 및 BoardDto 추출
            return this.boardService.createBoard(boardCreateForm, _member);
        }
    }

    /**
     * 게시글 상세
     *
     * @param id: 게시글 id
     * @return BoardDto: 게시글 정보
     * @throws ResponseStatusException: 게시글 부재
     */
    @GetMapping("/{id}")
    public BoardDto detailBoard(@PathVariable("id") Long id) {
        // 1. 상세 게시글 추출
        return boardService.findBoardById(id);
    }

    /**
     * 게시글 상세 이미지
     *
     * @param id: 게시글 id
     * @return Resource: 이미지 바이너리 파일
     * @throws CustomException
     * @throws IOException: 파일 입출력, ResponseStatusException: 게시글 부재, FileNotFoundException: 이미지 파일 부재, 파일 부재
     */
    @GetMapping("/{id}/image")
    public Resource detailBoardImage(@PathVariable("id") Long id) throws IOException {
        // 1. Board 추출 (실패시, 404 반환)
        BoardDto boardDto = boardService.findBoardById(id);

        // 2. 파일이 존재한다면 이미지 추출 (실패시 404, 500 반환)
        if (boardDto.getSavedFile() != null) {
            return boardService.getImage(boardDto);
        }

        // 3. 그외 반환
        else {
            throw new FileNotFoundException("파일이 존재하지 않습니다.");
        }
    }

    /**
     * 게시글 삭제
     * 게시글 부재시 BOARD_NOTFOUND
     * 삭제 권한 검증 실패시 403
     */
    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable("id") Long id, Authentication authentication) throws CustomException {

        // 1. Board 추출 (실패시 404 반환)
        Board board = this.boardService.getBoard(id);

        // 2. 삭제 권한 검증: 작성자 or ADMIN or SUPERVISOR
        if (SecurityUtils.isWriter(authentication, board) || SecurityUtils.isAdmin(authentication) || SecurityUtils.isSupervisor(authentication)) {
            boardService.deleteBoardById(id);
        }
        // 권한 없을 경우
        else {
            throw new AccessDeniedException("삭제 권한이 없습니다."); // 403
        }
    }

    /**
     * 게시글 수정
     * 게시글 부재시 BOARD_NOTFOUND
     * 삭제 권한 검증 실패시 NO_AUTHORIZATION
     */
    @PutMapping("/{id}")
    public BoardDto updateBoard(@PathVariable("id") Long id,
                                @ModelAttribute BoardUpdateForm boardUpdateForm,
                                Authentication authentication) throws CustomException, IOException {

        // 1. 게시글 추출 (실패시 404 반환)
        Board board = boardService.getBoard(id);

        // 2. 수정 권한 검증 (실패시 403 반환)
        if (!SecurityUtils.isWriter(authentication, board)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        // 3. 게시글 수정 및 200 반환
        else {
            BoardDto updatedBoard = boardService.updateBoard(board, boardUpdateForm);
            return updatedBoard;
        }
    }

    /**
     * 게시글 수정 권한 검증
     * 게시글 부재시 BOARD_NOTFOUND 반환
     * 수정 권한 검증 실패시 NO_AUTHORIZATION
     */
    @GetMapping("/{id}/check")
    public void checkUpdateAuth(@PathVariable("id") Long id,
                                Authentication authentication) throws CustomException {
        // 1. Board 추출 (실패시 404 반환)
        Board board = this.boardService.getBoard(id);

        if (SecurityUtils.isWriter(authentication, board)) {
            // 2. 수정 권한 검증 (실패시 403 반환)
            throw new AccessDeniedException("수정 권한이 없습니다."); // 403
        }
    }

    /**
     * 상세 게시판 댓글 조회
     * 게시글 부재시 BOARD_NOTFOUND 반환
     */
    @GetMapping("/{id}/comment")
    public List<CommentDto> commentList(@PathVariable("id") Long id) throws CustomException {
        // 1. boardId 로 해당 게시글 댓글 조회 (실패시 404 반환)
        List<CommentDto> commentDtoList = commentService.getCommentList(id);

        // 2. 댓글 조회 성공시 200 반환
        return commentDtoList;
    }

    /**
     * 댓글 작성
     * 게시글 부재시 BOARD_NOTFOUND 반환
     * 빈칸 입력시 ONLY_BLANk 반환
     */
    @PostMapping("/{id}/comment")
    public CommentDto createComment(@PathVariable("id") Long id,
                                    @RequestBody CommentCreateForm commentCreateForm,
                                    Authentication authentication) throws CustomException {
        // 1. userId 추출
        String _userId = authentication.getName();

        // 2. Member 추출 (실패시 401 반환)
        Member member = memberService.getMemberByUserId(_userId);

        // 3. Board 추출 (실패시 404 반환)
        Board board = this.boardService.getBoard(id);

        // 4. 빈 내용 유효성 검사 (실패시 400 반환)
        if (commentCreateForm.getContents().trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.ONLY_BLANk);
        } else {
            // 5. 댓글 작성 성공시 200 반환
            CommentDto commentDto = commentService.createComment(commentCreateForm, member, board);
            return commentDto;
        }
    }

    /**
     * 게시판 파일 다운로드
     * 파일 부재시 BOARD_NOTFOUND
     * 파일 추출 실패시 FILE_IOFAILED
     * 리소스 추출 후 파일명을 UTF8로 변환 후 CONTENT_DISPOSITION에 담아 반환
     */
    @GetMapping("/{id}/file")
    @CrossOrigin(value = {"*"}, exposedHeaders = {"Content-Disposition"})
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws CustomException, IOException {

        // 1. boardDto 추출 (실패시, 404 반환)
        BoardDto boardDto = boardService.findBoardById(id);

        // 2. Resouce 추출
        Resource resource = boardService.getDownloadResource(boardDto);

        // 3. 파일명 UTF-8로 인코딩 설정 (한글 깨짐 방지)
        // 브라우저별 encoding 방식을 다르게 해야함 (추후 수정)
        String originalFileName = boardDto.getOriginalFile();
        String encodedOriginalFileName = URLEncoder.encode(originalFileName, "UTF-8");

        String contentDisposition = "attachment; filename=\"" + encodedOriginalFileName + "\"";

        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition) // Content-Disposition: 브라우저에게 응답으로 리소스가 다운로드 되어야 함을 명시
                .body(resource);
    }
}