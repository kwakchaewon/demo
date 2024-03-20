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
     * Admin 전용
     * USER 조회
     */
    @GetMapping(value = "/members")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> pagingMemberList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable
    ) {
        Map<String, Object> data = memberService.getMemberList(pageable);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /**
     * Admin: USER 삭제
     * Super: USER, ADMIN 삭제
     */
    @DeleteMapping(value = "/member/{id}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_ADMIN')")
    public ResponseEntity deleteMember(@PathVariable("id") Long id) throws CustomException {
        memberService.deleteMemberById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     *  Super 전용
     * 사용자 + 관리자 조회
     */
    @GetMapping(value = "/auth")
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> pagingAdminList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable
    ) {
        Map<String, Object> data = memberService.getAdminList(pageable);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}