package com.example.demo.controller;

import com.example.demo.dto.response.AdminMemberDto;
import com.example.demo.dto.response.MemberDto;
import com.example.demo.entity.Member;
import com.example.demo.service.MemberService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Access;
import java.util.List;

@RequestMapping("/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {
    private final MemberService memberService;

    @GetMapping(value = "")
    public List<AdminMemberDto> memberList(){
        return memberService.getMemberList();
    }

    @DeleteMapping(value = "/member/{id}")
    public ResponseEntity deleteMember(@PathVariable("id") Long id, @RequestHeader("Access_TOKEN") String authorizationHeader){
        // 권한 검증 필요
        memberService.deleteMemberById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
