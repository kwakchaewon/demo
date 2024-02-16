package com.example.demo;

import com.example.demo.entity.Board;
import com.example.demo.entity.User;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoApplicationTests {
	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private UserRepository userRepository;
	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/**
	 *  Board 테스트 데이터 300개 생성
	 */
	@Test
	void createBoard(){
		for(int i = 1; i<=300; i++){
			String title = String.format("테스트 데이터: [%03d]",i);
			String content = String.format("[%03d]번째 게시글",i);
			Board board = new Board(title, content);
			this.boardRepository.save(board);
		}
	}

	/**
	 *  admin user 생성
	 */
	@Test
	void createTestUser(){
		String encPw = passwordEncoder.encode("admin");
		User testUser = User.builder()
				.userId("admin")
				.userPw(encPw)
				.userName("admin")
				.build();

		userRepository.save(testUser);
	}

	/**
	 * 유저 정보 검색 후 비밀번호 비교
	 */
	@Test
	void test_2(){
		String encPassword = passwordEncoder.encode("admin");

		User user = userRepository.findByUserId("admin")
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

		assertThat(user.getUserPw()).isEqualTo(encPassword);
	}

}
