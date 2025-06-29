package com.example.studylearnspringposts.controller;

import com.example.studylearnspringposts.dto.UserRequestDto;
import com.example.studylearnspringposts.dto.UserResponseDto;
import com.example.studylearnspringposts.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 회원가입 API
     * - username: 4~10자, 소문자+숫자
     * - password: 8~15자, 대소문자+숫자+특수문자
     * - 중복 체크 후 회원 저장
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody UserRequestDto userRequestDto) {
        try {
            // 유효성 검사
            if (!userRequestDto.isValidUsername()) {
                UserResponseDto errorResponse = UserResponseDto.builder()
                        .message("사용자명은 4~10자의 소문자 알파벳과 숫자로만 구성되어야 합니다")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            if (!userRequestDto.isValidPassword()) {
                UserResponseDto errorResponse = UserResponseDto.builder()
                        .message("비밀번호는 8~15자의 대소문자, 숫자, 특수문자를 모두 포함해야 합니다")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            UserResponseDto response = userService.signup(userRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            UserResponseDto errorResponse = UserResponseDto.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * 로그인 API
     * - username/password 검증
     * - JWT 토큰 발급
     * - 성공 시 토큰을 헤더에 추가하여 반환
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserRequestDto userRequestDto) {
        try {
            UserResponseDto response = userService.loginWithJwt(
                userRequestDto.getUsername(), 
                userRequestDto.getPassword()
            );
            
            // JWT 토큰을 헤더에 추가
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + response.getToken())
                    .body(response);
                    
        } catch (IllegalArgumentException e) {
            UserResponseDto errorResponse = UserResponseDto.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
} 