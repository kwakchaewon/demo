package com.example.demo.repository;

import com.example.demo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);
    @Modifying
    @Query(value = "INSERT INTO member (user_id, user_pw, email) VALUES (:userId, :userPw, :email)",nativeQuery = true)
    void insertMember(@Param("userId") String userId, @Param("userPw") String userPw, @Param("email")String email);
}
