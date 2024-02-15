package com.example.demo;

import com.example.demo.entity.Board;
import com.example.demo.repository.BoardRepository;
import com.example.demo.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {
	@Autowired
	private BoardRepository boardRepository;

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

}
