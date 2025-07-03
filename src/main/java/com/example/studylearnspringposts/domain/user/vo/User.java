package com.example.studylearnspringposts.domain.user.vo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false ,unique = true, length = 12)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role; // USER , ADMIN , OTHERS

    private Integer age;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;

    }

    public User () {

    }
}
