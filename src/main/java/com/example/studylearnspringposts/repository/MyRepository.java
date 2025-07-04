package com.example.studylearnspringposts.repository;

import com.example.studylearnspringposts.domain.user.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MyRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.age >= :age")
    List<User> findByAge(@Param("age") Integer age);

    User findByUsernameAndPassword(String username, String password);
    
    // 회원가입 및 로그인용 메서드 추가
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);


}
