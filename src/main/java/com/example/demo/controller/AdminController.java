package com.example.demo.controller;

import com.example.demo.dto.response.AdminMemberDto;
import com.example.demo.service.MemberService;
import com.example.demo.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RequestMapping("/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {
    private final MemberService memberService;

    /**
     * Admin 페이지 회원 관리
     */
    @GetMapping(value = "/members")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> pagingMemberList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable
    ){
        // authentication 정보 추출 (어노테이션에서 사용자일 경우를 증명하지 못해서...)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String auth = authentication.getAuthorities().stream().findFirst().get().getAuthority();

        // SUPERVISOR 일 경우 USER + ADMIN 조회
        if (auth.equals("ROLE_SUPERVISOR")){
            Map<String, Object> data = memberService.getMemberList3(pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }
        // ADMIN 일 경우 USER 만 조회
        else {
            Map<String, Object> data = memberService.getMemberList2(pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/member/{id}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_ADMIN')")
    public ResponseEntity deleteMember(@PathVariable("id") Long id) throws CustomException {
        memberService.deleteMemberById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 사용자 + 관리자 권한 조회
     */
    @GetMapping(value = "/auth")
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> pagingAdminList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable
    ){
        Map<String, Object> data = memberService.getAdminList(pageable);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
