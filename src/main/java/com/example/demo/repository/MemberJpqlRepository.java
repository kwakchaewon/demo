package com.example.demo.repository;

import com.example.demo.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class MemberJpqlRepository {
    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public Optional<Member> findMemberById(String userId){
        String jpql = "SELECT m FROM Member m WHERE m.userId = :userId";
        Member member = entityManager
                .createQuery(jpql, Member.class)
                .setParameter("userId", userId)
                .getResultStream().findFirst().orElse(null);

        return Optional.ofNullable(member);
    }
}
