package com.example.studylearnspringposts.repository;

import com.example.studylearnspringposts.domain.user.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyRepository extends JpaRepository<User, Long> {

    @Query("SELECT user FROM User A WHERE User.age >= :age")
    List<User> findByAge(@Param("age") Integer age);

}
