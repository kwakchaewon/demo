package com.example.demo;

import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.MemberService;
import com.example.demo.util.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Random;
import java.util.zip.DataFormatException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoApplicationTests {
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MemberService memberService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	JWTUtil jwtUtil;

	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/**
	 *  test user 10명 생성
	 */
	@Test
	void createTestUser(){

		for (int i = 1; i<= 10; i++){
			String encPw = passwordEncoder.encode("user");

			Member testMember = Member.builder()
					.userId("admin"+i)
					.userPw(encPw)
					.userName("admin"+i)
					.build();
			memberRepository.save(testMember);
		}
	}

	/**
	 *  Board 테스트 데이터 300개 생성
	 */
	@Test
	void createBoard() throws Exception {
		for(int i = 1; i<=120; i++) {
//            Random random = new Random();
//            long randomId = random.nextInt(5) + 1L;
//            Member testMember = memberService.getMember(randomId);

            String title = String.format("테스트 데이터: [%03d]", i);
            String content = String.format("[%03d]번째 게시글", i);
            Board board = new Board(title, content);
            this.boardRepository.save(board);
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
	@Test
	void createToken(){
		String userId = "admin";
		String userName = "admin";

		String token = jwtUtil.createToken(userId, userName);

		System.out.println("Token : " + token);

		assertThat(jwtUtil.decodeToken(token).getClaim("userName").asString()).isEqualTo(userName);
	}
}
