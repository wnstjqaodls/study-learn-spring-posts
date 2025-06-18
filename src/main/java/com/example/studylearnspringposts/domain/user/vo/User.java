package com.example.studylearnspringposts.domain.user.vo;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false ,unique = true, length = 12)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role; // USER , ADMIN , OTHERS

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;

    }

    public User () {

    }
}
