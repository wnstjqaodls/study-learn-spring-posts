package com.example.studylearnspringposts.controller;

import com.example.studylearnspringposts.dto.PostRequestDto;
import com.example.studylearnspringposts.dto.PostResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * API Gateway HTTP 통합 테스트
 * 실제 HTTP 요청/응답을 통한 전체 스택 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@Transactional
class ApiGatewayIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(ApiGatewayIntegrationTest.class);

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Long createdPostId;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 테스트용 게시글 생성
        PostRequestDto createRequest = PostRequestDto.builder()
                .title("API 통합테스트 게시글")
                .author("API 테스터")
                .content("API 통합테스트 내용")
                .password("api1234")
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        PostResponseDto createdPost = objectMapper.readValue(
                result.getResponse().getContentAsString(), 
                PostResponseDto.class
        );
        createdPostId = createdPost.getId();
        
        log.info("=== API 테스트용 게시글 생성 완료 - ID: {} ===", createdPostId);
    }

    @Test
    @DisplayName("GET /api/v1/health - 헬스체크 API 테스트")
    void shouldReturnHealthCheck() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("API Gateway is running"));
        
        log.info("헬스체크 API 테스트 완료");
    }

    @Test
    @DisplayName("POST /api/v1/posts - 게시글 생성 API 테스트")
    void shouldCreatePostViaAPI() throws Exception {
        // Given
        PostRequestDto newPost = PostRequestDto.builder()
                .title("새로운 API 게시글")
                .author("새로운 작성자")
                .content("새로운 내용")
                .password("newpass123")
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPost)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("새로운 API 게시글"))
                .andExpect(jsonPath("$.author").value("새로운 작성자"))
                .andExpect(jsonPath("$.content").value("새로운 내용"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.writeDate").exists())
                .andReturn();

        log.info("게시글 생성 API 테스트 완료");
    }

    @Test
    @DisplayName("GET /api/v1/posts/{id} - 특정 게시글 조회 API 테스트")
    void shouldGetPostByIdViaAPI() throws Exception {
        mockMvc.perform(get("/api/v1/posts/{id}", createdPostId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdPostId))
                .andExpect(jsonPath("$.title").value("API 통합테스트 게시글"))
                .andExpect(jsonPath("$.author").value("API 테스터"))
                .andExpect(jsonPath("$.content").value("API 통합테스트 내용"));

        log.info("게시글 조회 API 테스트 완료");
    }

    @Test
    @DisplayName("GET /api/v1/posts - 전체 게시글 목록 조회 API 테스트")
    void shouldGetAllPostsViaAPI() throws Exception {
        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1)) // setUp에서 생성한 1개
                .andExpect(jsonPath("$[0].id").value(createdPostId));

        log.info("전체 게시글 조회 API 테스트 완료");
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{id} - 게시글 수정 API 테스트")
    void shouldUpdatePostViaAPI() throws Exception {
        // Given
        PostRequestDto updateRequest = PostRequestDto.builder()
                .title("수정된 API 제목")
                .author("수정된 API 작성자")
                .content("수정된 API 내용")
                .password("api1234") // 올바른 비밀번호
                .build();

        // When & Then
        mockMvc.perform(put("/api/v1/posts/{id}", createdPostId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdPostId))
                .andExpect(jsonPath("$.title").value("수정된 API 제목"))
                .andExpect(jsonPath("$.author").value("수정된 API 작성자"))
                .andExpect(jsonPath("$.content").value("수정된 API 내용"));

        log.info("게시글 수정 API 테스트 완료");
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{id} - 잘못된 비밀번호로 수정 시도시 400 에러")
    void shouldFailUpdateWithWrongPasswordViaAPI() throws Exception {
        // Given
        PostRequestDto wrongPasswordRequest = PostRequestDto.builder()
                .title("해킹 시도")
                .author("해커")
                .content("해킹 내용")
                .password("wrongpassword")
                .build();

        // When & Then
        mockMvc.perform(put("/api/v1/posts/{id}", createdPostId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isBadRequest()); // 400 Bad Request 예상

        log.info("잘못된 비밀번호 수정 차단 API 테스트 완료");
    }

    @Test
    @DisplayName("DELETE /api/v1/posts/{id} - 게시글 삭제 API 테스트")
    void shouldDeletePostViaAPI() throws Exception {
        // Given
        PostRequestDto deleteRequest = PostRequestDto.builder()
                .password("api1234") // 올바른 비밀번호
                .build();

        // When & Then
        mockMvc.perform(delete("/api/v1/posts/{id}", createdPostId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("게시글이 성공적으로 삭제되었습니다."));

        // 삭제 확인
        mockMvc.perform(get("/api/v1/posts/{id}", createdPostId))
                .andExpect(status().isNotFound()); // 404 Not Found 예상

        log.info("게시글 삭제 API 테스트 완료");
    }

    @Test
    @DisplayName("전체 CRUD 시나리오 API 테스트")
    void shouldHandleFullCRUDScenarioViaAPI() throws Exception {
        log.info("=== 전체 CRUD 시나리오 API 테스트 시작 ===");

        // 1. CREATE
        PostRequestDto createRequest = PostRequestDto.builder()
                .title("시나리오 테스트 게시글")
                .author("시나리오 테스터")
                .content("시나리오 테스트 내용")
                .password("scenario123")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        PostResponseDto createdPost = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                PostResponseDto.class
        );
        Long postId = createdPost.getId();
        log.info("1단계: 게시글 생성 완료 - ID: {}", postId);

        // 2. READ
        mockMvc.perform(get("/api/v1/posts/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("시나리오 테스트 게시글"));
        log.info("2단계: 게시글 조회 완료");

        // 3. UPDATE
        PostRequestDto updateRequest = PostRequestDto.builder()
                .title("업데이트된 시나리오 제목")
                .author("업데이트된 작성자")
                .content("업데이트된 내용")
                .password("scenario123")
                .build();

        mockMvc.perform(put("/api/v1/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("업데이트된 시나리오 제목"));
        log.info("3단계: 게시글 수정 완료");

        // 4. DELETE
        PostRequestDto deleteRequest = PostRequestDto.builder()
                .password("scenario123")
                .build();

        mockMvc.perform(delete("/api/v1/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk());
        log.info("4단계: 게시글 삭제 완료");

        // 5. DELETE 확인
        mockMvc.perform(get("/api/v1/posts/{id}", postId))
                .andExpect(status().isNotFound());
        log.info("5단계: 삭제 확인 완료");

        log.info("=== 전체 CRUD 시나리오 API 테스트 완료 ===");
    }
} 
