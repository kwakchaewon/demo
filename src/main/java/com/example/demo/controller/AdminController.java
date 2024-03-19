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
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RequestMapping("/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {
    private final MemberService memberService;

    @GetMapping(value = "/members")
    public ResponseEntity<Map<String, Object>> pagingMemberList(
            @PageableDefault(sort = {"id"}, page = 0) Pageable pageable
    ){
        Map<String, Object> data = memberService.getMemberList2(pageable);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @DeleteMapping(value = "/member/{id}")
    public ResponseEntity deleteMember(@PathVariable("id") Long id, @RequestHeader("Access_TOKEN") String authorizationHeader) throws CustomException {
        // 권한 검증 필요
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
