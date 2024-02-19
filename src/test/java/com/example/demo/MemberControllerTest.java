package com.example.demo;

import com.example.demo.controller.MemberController;
import com.example.demo.repository.MemberRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {
    @Autowired
    private MemberRepository memberRepository;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    MemberController memberController;

    @Autowired
    private MockMvc mockMvc;

    /**
     *  admin user 생성
     */
//    @Test
//    void createTestUser(){
//        String encPw = passwordEncoder.encode("admin");
//        Member testMember = Member.builder()
//                .userId("admin")
//                .userPw(encPw)
//                .userName("admin")
//                .build();
//
//        userRepository.save(testMember);
//    }

//    @DisplayName("1. 로그인 실패 테스트")
//    @Test
//    void test_1() throws Exception {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("user_id", "test_userr");
//        jsonObject.put("user_pw", "test_passwordd");
//
//        ResultActions result = mockMvc.perform(post("/user/login")
//                .content(jsonObject.toString())
//                .contentType(MediaType.APPLICATION_JSON));
//
//        MvcResult mvcResult = result.andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println(mvcResult.getResponse().getContentAsString());
//    }
//
//    @DisplayName("2. 로그인 성공 테스트")
//    @Test
//    void test_2() throws Exception {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("user_id", "admin");
//        jsonObject.put("user_pw", "admin");
//
//        ResultActions result = mockMvc.perform(post("/member/login")
//                .content(jsonObject.toString())
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED));
//
//        MvcResult mvcResult = result.andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println(mvcResult.getResponse().getContentAsString());
//    }
}
