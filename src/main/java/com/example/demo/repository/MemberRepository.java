package com.example.demo.repository;

import com.example.demo.dto.response.AdminMemberDto;
import com.example.demo.dto.response.MemberDto;
import com.example.demo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);
    @Modifying
    @Query(value = "INSERT INTO member (user_id, user_pw, email, created_at) VALUES (:userId, :userPw, :email, :createdAt)",nativeQuery = true)
    void insertMember(@Param("userId") String userId, @Param("userPw") String userPw, @Param("email") String email, @Param("createdAt") LocalDateTime createdAt);

    Boolean existsByUserId(String userId);
    Boolean existsByEmail(String email);
    List<AdminMemberDto> findAllAdminMemberDtoByGrantedAuth(String GrantedAuth);
}
