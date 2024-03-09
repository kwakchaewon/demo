package com.example.demo.controller;

import com.example.demo.dto.request.BoardCreateForm;
import com.example.demo.dto.request.BoardUpdateForm;
import com.example.demo.dto.request.CommentCreateForm;
import com.example.demo.dto.response.BoardDto;
import com.example.demo.dto.response.CommentDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.MemberService;
import com.example.demo.util.FileStore;
import com.example.demo.util.JWTUtil;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RequestMapping("/board")
@RestController
@Validated
@RequiredArgsConstructor
public class BoardController {

    @Value("${jwt.secret_access}")
    private String secret_access;

    @Autowired
    private BoardService boardService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CommentService commentService;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    private FileStore fileStore;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     * 게시글 작성
     */
    @PostMapping(value = "")
    public ResponseEntity<BoardDto> createBoardDone(@ModelAttribute BoardCreateForm boardCreateForm,
                                                    @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {

        // 1. 빈 제목, 내용 유효성 검사 (실패시, 400 반환)

        if (boardCreateForm.getTitle().trim().isEmpty() || boardCreateForm.getContents().trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, Constants.ExceptionClass.ONLY_BLANk);
        } else {
            String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);
            Member _member = this.memberService.getMemberByUserId(_userId);
            BoardDto boardDto = this.boardService.createBoard(boardCreateForm, _member);
            return new ResponseEntity<>(boardDto, HttpStatus.OK);
        }
    }

    /**
     * 게시글 상세
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> detailBoard(@PathVariable("id") Long id) throws CustomException {
        // 1. 게시글 상세
        BoardDto boardDto = boardService.findBoardById(id);

//        // 2. 사진 미리보기 resource

        //2_1. 첨부파일이 존재하고 확장자가 사진일 경우
//        if(boardDto.getSavedFile()!= null){
//            String strPath = fileStore.getFullPath(boardDto.getSavedFile());
//
//            if (fileStore.isImage(strPath)) {
//                Path filePath = Paths.get(strPath);
//                Resource resource = new InputStreamResource(Files.newInputStream(filePath));
//                boardDto.setImgFile(resource);
//                return ResponseEntity.ok().body(boardDto);
//            }
//            return ResponseEntity.ok().body(boardDto);
//        }
//        else {
//            return ResponseEntity.ok().body(boardDto);
//        }
//        //2_2. 리소스에 담아서 보내줌.


        return new ResponseEntity<>(boardDto, HttpStatus.OK);
//            return ResponseEntity.ok().body();
    }

    /**
     * 게시판 상세 이미지 출력
     */
    @GetMapping("/{id}/image")
    public ResponseEntity detailBoardImage(@PathVariable("id") Long id) throws CustomException {
        // 1. Board 추출 (실패시, 404 반환)
        BoardDto boardDto = boardService.findBoardById(id);

        // 2. 파일이 존재한다면 이미지 추출 (실패시 404, 500 반환)
        if(boardDto.getSavedFile()!= null){
            Resource resource = boardService.getImage(boardDto);
            return new ResponseEntity(resource, HttpStatus.OK);
        }

        // 3. 그외 400 반환
        else {
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.FILE_NOTFOUND);
        }

//            String strPath = fileStore.getFullPath(boardDto.getSavedFile());
//
//            if (fileStore.isImage(strPath)) {
//                Path filePath = Paths.get(strPath);
//                try {
//                    Resource resource = new InputStreamResource(Files.newInputStream(filePath));
//                    return ResponseEntity.ok().body(resource);
//                }catch (Exception e){
//                    System.out.println("error = " + e);
//                    throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ExceptionClass.FILE_IOFAILED);
//                }
//            }
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteBoard(@PathVariable("id") Long id,
                                      @RequestHeader("Access_TOKEN") String authorizationHeader) throws CustomException {

        // 1. userId 추출
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);

        // 2. Board 추출 (실패시 404 반환)
        Board board = this.boardService.getBoard(id);

        // 3. 삭제 권한 검증 (실패시 403 반환)
        if (!board.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, Constants.ExceptionClass.NO_AUTHORIZATION);
        } else {
            // 4. 게시글 삭제 및 200 반환
            boardService.deleteBoardById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoardDto> updateBoard(@PathVariable("id") Long id,
                                                @ModelAttribute BoardUpdateForm boardUpdateForm,
                                                @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException, IOException {
        // 1. userId 추출

        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);

        // 2. Board 추출 (실패시 404 반환)
        Board board = boardService.getBoard(id);

        // 3. 수정 권한 검증 (실패시 403 반환)
        if (!board.getMember().getUserId().equals(_userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, Constants.ExceptionClass.NO_AUTHORIZATION);
        } else {
            // 4. 게시글 수정 및 200 반환
            BoardDto updatedBoard = boardService.updateBoard(board, boardUpdateForm);
            return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
        }
    }

    /**
     * 페이징 기반 게시판 목록
     */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> pagingBoardList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable
    ) {
        Map<String, Object> data = boardService.getBoardList(pageable);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /**
     * 게시글 수정 권한 검증
     */
    @GetMapping("/{id}/check")
    public ResponseEntity checkUpdateAuth(@PathVariable("id") Long id,
                                          @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {
        // 1. userId 추출
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);

        // 2. Board 추출 (실패시 404 반환)
        Board board = this.boardService.getBoard(id);

        if (!board.getMember().getUserId().equals(_userId)) {
            // 3. 수정 권한 검증 (실패시 403 반환)
            throw new CustomException(HttpStatus.FORBIDDEN, Constants.ExceptionClass.NO_AUTHORIZATION);
        } else {
            // 4. 검증 성공시 200 반환
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * 상세 게시판 댓글 조회
     */
    @GetMapping("/{id}/comment")
    public ResponseEntity<List<CommentDto>> commentList(@PathVariable("id") Long id) throws CustomException {
        // 1. boardId 로 해당 게시글 댓글 조회 (실패시 404 반환)
        List<CommentDto> commentDtoList = commentService.getCommentList(id);

        // 2. 댓글 조회 성공시 200 반환
        return new ResponseEntity<>(commentDtoList, HttpStatus.OK);
    }

    /**
     * 댓글 작성
     */
    @PostMapping("/{id}/comment")
    public ResponseEntity<CommentDto> createComment(@PathVariable("id") Long id,
                                                    @RequestBody CommentCreateForm commentCreateForm,
                                                    @RequestHeader("ACCESS_TOKEN") String authorizationHeader) throws CustomException {
        // 1. userId 추출
        String _userId = jwtUtil.getUserIdByToken(authorizationHeader, secret_access);

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
            return new ResponseEntity<>(commentDto, HttpStatus.OK);
        }
    }

    /**
     * 게시판 파일 다운로드
     */
    @GetMapping("/{id}/file")
    @CrossOrigin(value = {"*"}, exposedHeaders = {"Content-Disposition"})
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws CustomException, IOException {

        // 1. boardDto 추출 (실패시, 404 반환)
        BoardDto boardDto = boardService.findBoardById(id);

        // 2. Resouce 추출
        Resource resource = boardService.getDownloadResource(boardDto);

//        String savedFileName = boardDto.getSavedFile();
//        String originalFileName = boardDto.getOriginalFile();
//
//        // 2. 파일 전체 경로 추출
//        Path filePath = Paths.get(fileStore.getFullPath(savedFileName));
//
//        // 3. 해당 파일로 응답 설정
//        Resource resource = new InputStreamResource(Files.newInputStream(filePath));

        // 3. 파일명 UTF-8로 인코딩 설정 (한글 깨짐 방지)
        // 브라우저별 encoding 방식을 다르게 해야함 (추후 수정)
        String originalFileName = boardDto.getOriginalFile();
        String encodedOriginalFileName = URLEncoder.encode(originalFileName,"UTF-8");
//        String encodedOriginalFileName = URLEncoder.encode(originalFileName,"UTF-8").replaceAll("\\+", "%20");
        String contentDisposition = "attachment; filename=\"" + encodedOriginalFileName + "\"";

        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition) // Content-Disposition: 브라우저에게 응답으로 리소스가 다운로드 되어야 함을 명시
                .body(resource);

    }
}