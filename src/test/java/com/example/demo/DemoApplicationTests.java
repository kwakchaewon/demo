package com.example.demo;

import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.BoardService;
import com.example.demo.service.MemberService;
import com.example.demo.util.JWTUtil;
import com.example.demo.util.exception.CustomException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.zip.DataFormatException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DemoApplicationTests {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private BoardService boardService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * supervisor 계정 생성
     */
    @Test
    @Order(3)
    void createSupervisor() {
        String encPw = passwordEncoder.encode("super");

        Member supervisor = Member.builder()
                .userId("super")
                .userPw(encPw)
                .email("super@naver.com")
                .createdAt(LocalDateTime.now())
                .grantedAuth("ROLE_SUPERVISOR")
                .build();
        memberRepository.save(supervisor);
    }

    /**
     * admin 계정 10개 생성
     */
    @Test
    @Order(2)
    void createAdmin() {
        for (int i = 1; i <= 10; i++) {
            String encPw = passwordEncoder.encode("admin" + i);

            Member admin = Member.builder()
                    .userId("admin" + i)
                    .userPw(encPw)
                    .email("admin" + i + "@naver.com")
                    .createdAt(LocalDateTime.now())
                    .grantedAuth("ROLE_ADMIN")
                    .build();
            memberRepository.save(admin);
        }
//        String encPw = passwordEncoder.encode("admin");
//
//        Member admin = Member.builder()
//                .userId("admin")
//                .userPw(encPw)
//                .email("admin@naver.com")
//                .createdAt(LocalDateTime.now())
//                .grantedAuth("ROLE_ADMIN")
//                .build();
//        memberRepository.save(admin);
    }

    /**
     * test user 120명 생성
     */
    @Test
    @Order(1)
    void createTestUser() {
        for (int i = 1; i <= 120; i++) {
            String encPw = passwordEncoder.encode("@a12345678");

            Member testMember = Member.builder()
                    .userId("test" + i)
                    .userPw(encPw)
                    .email("test" + i + "@naver.com")
                    .createdAt(LocalDateTime.now())
                    .grantedAuth("ROLE_USER")
                    .build();
            memberRepository.save(testMember);
        }
    }

    /**
     * Board 테스트 데이터 50개 생성
     */
    @Test
    @Order(4)
    void createBoard() {
        for (int i = 1; i <= 200; i++) {
            Random random = new Random();
            long randomId = random.nextInt(9) + 1L;

            Member testMember = memberService.getMember(randomId);
            String title = String.format("테스트 데이터: [%03d]", i);
            String content = String.format("[%03d]번째 게시글", i);
            Board board = new Board(title, content, testMember);
            this.boardRepository.save(board);
        }
    }

    /**
     * Comment 테스트 데이터 50개 생성
     */
    @Test
    @Order(5)
    void createComment() throws CustomException {
        for (int i = 1; i <= 50; i++) {
            // 작성자 랜덤
            Random memberRd = new Random();
            long memberId = memberRd.nextInt(9) + 1L;

            Random boardRd = new Random();
            long boardId = memberRd.nextInt(49) + 1L;

            Member _member = memberService.getMember(memberId);
            Board _board = boardService.getBoard(boardId);

            String contents = String.format("테스트 [%03d] 댓글", i);
            Comment comment = new Comment(contents, _member, _board);

            this.commentRepository.save(comment);
        }
    }

    /**
     * 유저 정보 검색 후 비밀번호 비교
     */
//	@Test
//	void checkPw(){
//		/*
//		String encPassword = passwordEncoder.encode("admin");
//
//		Member member = userRepository.findByUserId("admin")
//				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
//
//		assertThat(member.getUserPw()).isEqualTo(encPassword);
//		*/
//
//		String userId = "admin";
//		String userPw = "admin";
//		UserDetails user = memberService.loadUserByUsername(userId);
//
//		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, userPw);
//		authenticationManager.authenticate(authenticationToken);
//
//		assertThat(authenticationToken.getCredentials()).isEqualTo(userPw);
//
//		System.out.println("getCredentials: " + authenticationToken.getCredentials());
//		System.out.println("userPw: " + userPw);
//	}

    /**
     * jwt 토큰 생성 및 디코딩 데이터 비교
     */
//	@Test
//	void createToken(){
//		String userId = "admin";
//		String userName = "admin";
//
//		String token = jwtUtil.createToken(userId, userName);
//
//		System.out.println("Token : " + token);
//
//		assertThat(jwtUtil.decodeToken(token).getClaim("userName").asString()).isEqualTo(userName);
//	}
}
