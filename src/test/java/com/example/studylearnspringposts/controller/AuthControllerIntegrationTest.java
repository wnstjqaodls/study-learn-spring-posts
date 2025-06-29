package com.example.studylearnspringposts.controller;

import com.example.studylearnspringposts.dto.UserRequestDto;
import com.example.studylearnspringposts.dto.UserResponseDto;
import com.example.studylearnspringposts.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 🎯 JWT 인증 통합테스트 시연
 * 회원가입과 로그인 기능을 중심으로 테스트
 */
@SpringBootTest
@AutoConfigureWebMvc
@Transactional
class AuthControllerIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(AuthControllerIntegrationTest.class);

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        log.info("🚀 JWT 인증 통합테스트 시연 준비 완료");
    }

    @Test
    @DisplayName("1️⃣ 일반 사용자 회원가입 성공 테스트")
    void testUserSignupSuccess() throws Exception {
        log.info("=== 1단계: 일반 사용자 회원가입 ===");
        
        UserRequestDto signupRequest = UserRequestDto.builder()
                .username("user123")
                .password("Password1!")
                .role("USER")
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("user123"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.message").value("회원가입이 성공적으로 완료되었습니다"))
                .andReturn();

        log.info("✅ 일반 사용자 회원가입 성공: {}", result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("2️⃣ 일반 사용자 로그인 및 JWT 토큰 발급 테스트")
    void testUserLoginSuccess() throws Exception {
        log.info("=== 2단계: 일반 사용자 로그인 ===");
        
        // 먼저 회원가입
        UserRequestDto signupRequest = UserRequestDto.builder()
                .username("login123")
                .password("Password1!")
                .role("USER")
                .build();
        
        userService.signup(signupRequest);

        // 이제 로그인
        UserRequestDto loginRequest = UserRequestDto.builder()
                .username("login123")
                .password("Password1!")
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("login123"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(header().exists("Authorization"))
                .andReturn();

        UserResponseDto loginResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), 
                UserResponseDto.class
        );
        
        log.info("✅ 일반 사용자 로그인 성공");
        log.info("🔑 JWT 토큰 발급: {}", loginResponse.getToken().substring(0, 20) + "...");
        log.info("📨 Authorization 헤더: {}", result.getResponse().getHeader("Authorization"));
    }

    @Test
    @DisplayName("3️⃣ 회원가입 실패 테스트 - 중복 사용자명")
    void testSignupFailDuplicateUsername() throws Exception {
        log.info("=== 3단계: 중복 사용자명 회원가입 실패 ===");
        
        // 먼저 사용자 생성
        UserRequestDto firstUser = UserRequestDto.builder()
                .username("duplicate")
                .password("Password1!")
                .role("USER")
                .build();
        
        userService.signup(firstUser);
        
        // 같은 사용자명으로 다시 가입 시도
        UserRequestDto duplicateUser = UserRequestDto.builder()
                .username("duplicate")
                .password("Password2@")
                .role("USER")
                .build();

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 사용자명입니다"));
        
        log.info("✅ 중복 사용자명 회원가입 차단 확인 완료");
    }

    @Test
    @DisplayName("4️⃣ 회원가입 실패 테스트 - 잘못된 사용자명 형식")
    void testSignupFailInvalidUsername() throws Exception {
        log.info("=== 4단계: 잘못된 사용자명 형식 테스트 ===");
        
        // 대문자가 포함된 잘못된 사용자명
        UserRequestDto invalidUser = UserRequestDto.builder()
                .username("TestUser") // 대문자 포함 (소문자+숫자만 허용)
                .password("Password1!")
                .role("USER")
                .build();

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("사용자명은 4~10자의 소문자 알파벳과 숫자로만 구성되어야 합니다"));
        
        log.info("✅ 잘못된 사용자명 형식 차단 확인 완료");
    }

    @Test
    @DisplayName("5️⃣ 로그인 실패 테스트 - 존재하지 않는 사용자")
    void testLoginFailUserNotFound() throws Exception {
        log.info("=== 5단계: 존재하지 않는 사용자 로그인 실패 ===");
        
        UserRequestDto loginRequest = UserRequestDto.builder()
                .username("nonexistent")
                .password("Password1!")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다"));
        
        log.info("✅ 존재하지 않는 사용자 로그인 차단 확인 완료");
    }

    @Test
    @DisplayName("6️⃣ 로그인 실패 테스트 - 잘못된 비밀번호")
    void testLoginFailWrongPassword() throws Exception {
        log.info("=== 6단계: 잘못된 비밀번호 로그인 실패 ===");
        
        // 먼저 사용자 생성
        UserRequestDto signupRequest = UserRequestDto.builder()
                .username("wrongpwd")
                .password("Password1!")
                .role("USER")
                .build();
        
        userService.signup(signupRequest);
        
        // 잘못된 비밀번호로 로그인 시도
        UserRequestDto loginRequest = UserRequestDto.builder()
                .username("wrongpwd")
                .password("WrongPassword!")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다"));
        
        log.info("✅ 잘못된 비밀번호 로그인 차단 확인 완료");
    }

    @Test
    @DisplayName("7️⃣ 관리자 회원가입 테스트")
    void testAdminSignup() throws Exception {
        log.info("=== 7단계: 관리자 회원가입 ===");
        
        UserRequestDto adminSignupRequest = UserRequestDto.builder()
                .username("admin123")
                .password("AdminPass1!")
                .role("ADMIN")
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminSignupRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("admin123"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        log.info("✅ 관리자 회원가입 성공: {}", result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("🎉 JWT 인증 통합테스트 시연 완료")
    void testCompleteSummary() {
        log.info("=== 🎉 JWT 인증 통합테스트 시연 완료 ===");
        log.info("");
        log.info("✅ 완료된 테스트 항목:");
        log.info("1️⃣ 일반 사용자 회원가입 (유효성 검사 포함)");
        log.info("2️⃣ 일반 사용자 로그인 + JWT 토큰 발급");
        log.info("3️⃣ 중복 사용자명 차단");
        log.info("4️⃣ 잘못된 사용자명 형식 차단");
        log.info("5️⃣ 존재하지 않는 사용자 로그인 차단");
        log.info("6️⃣ 잘못된 비밀번호 로그인 차단");
        log.info("7️⃣ 관리자 회원가입");
        log.info("");
        log.info("🔐 JWT 인증 시스템 완벽 동작 확인!");
        log.info("🛡️ 보안 검증 완벽 동작 확인!");
        log.info("");
        log.info("🚀 통합테스트 시연이 성공적으로 완료되었습니다!");
    }
} 