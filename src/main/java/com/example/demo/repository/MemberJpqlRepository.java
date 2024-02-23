package com.example.demo.repository;

import com.example.demo.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class MemberJpqlRepository {
    @Autowired
    private EntityManager entityManager;

//    public void insertMemberByJpql(String user_id, String user_pw, String email){
//        String jpql = "INSERT INTO Member  m (m.userId, m.userPw, m.email) VALUES (:username, :user_pw, :email)";
//        entityManager.createQuery(jpql)
//                .setParameter("user_id", user_id)
//                .setParameter("user_pw", user_pw)
//                .setParameter("email", email)
//                .executeUpdate();
//    }

    public Optional<Member> findMemberById(String userId){
        String jpql = "SELECT m FROM Member m WHERE m.userId = :userId";
        Member member = entityManager
                .createQuery(jpql, Member.class)
                .setParameter("userId", userId)
                .getResultStream().findFirst().orElse(null);

        return Optional.ofNullable(member);
    }
}
