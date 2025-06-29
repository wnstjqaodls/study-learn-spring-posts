package com.example.studylearnspringposts.controller;

import com.example.studylearnspringposts.dto.PostRequestDto;
import com.example.studylearnspringposts.dto.PostResponseDto;
import com.example.studylearnspringposts.exception.PostNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * PostController 통합 테스트
 * 실제 DB와 연동하여 전체 플로우를 테스트
 */
@SpringBootTest
@Transactional // 테스트 후 데이터 자동 롤백
class PostControllerIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(PostControllerIntegrationTest.class);

    @Autowired
    private PostController postController; // Mock 없이 실제 Bean 주입

    private PostResponseDto testPost;

    @BeforeEach
    void setUp() {
        log.info("=== 테스트 데이터 준비 ===");
        
        PostRequestDto createRequest = PostRequestDto.builder()
                .title("통합테스트 게시글")
                .author("테스터")
                .content("통합테스트 내용")
                .password("test1234")
                .build();

        testPost = postController.createPost(createRequest);
        log.info("테스트용 게시글 생성 완료 - ID: {}", testPost.getId());
    }

    @Test
    @DisplayName("게시글 생성 -> 조회 -> 수정 -> 삭제 전체 플로우 테스트")
    void shouldHandleFullPostLifecycle() {
        log.info("=== 전체 라이프사이클 테스트 시작 ===");

        // 1. 조회 테스트
        log.info("1단계: 게시글 조회");
        PostResponseDto foundPost = postController.getPostById(testPost.getId());
        assertThat(foundPost.getTitle()).isEqualTo("통합테스트 게시글");
        assertThat(foundPost.getAuthor()).isEqualTo("테스터");

        // 2. 수정 테스트
        log.info("2단계: 게시글 수정");
        PostRequestDto updateRequest = PostRequestDto.builder()
                .title("수정된 제목")
                .author("수정된 작성자")
                .content("수정된 내용")
                .password("test1234") // 올바른 비밀번호
                .build();

        PostResponseDto updatedPost = postController.updatePost(testPost.getId(), updateRequest);
        assertThat(updatedPost.getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedPost.getAuthor()).isEqualTo("수정된 작성자");

        // 3. 삭제 테스트
        log.info("3단계: 게시글 삭제");
        PostRequestDto deleteRequest = PostRequestDto.builder()
                .password("test1234")
                .build();

        postController.deletePost(testPost.getId(), deleteRequest);

        // 4. 삭제 확인
        log.info("4단계: 삭제 확인");
        assertThrows(PostNotFoundException.class, () -> {
            postController.getPostById(testPost.getId());
        });

        log.info("=== 전체 라이프사이클 테스트 완료 ===");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 수정 시도시 예외 발생")
    void shouldFailUpdateWithWrongPassword() {
        // Given
        PostRequestDto wrongPasswordRequest = PostRequestDto.builder()
                .title("해킹시도")
                .author("해커")
                .content("해킹내용")
                .password("wrongpassword")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            postController.updatePost(testPost.getId(), wrongPasswordRequest);
        });
        
        log.info("잘못된 비밀번호 수정 차단 확인 완료");
    }

    @Test
    @DisplayName("여러 게시글 생성 후 전체 조회 테스트")
    void shouldRetrieveAllPostsOrderedByDate() {
        // Given - 추가 게시글 3개 생성
        for (int i = 1; i <= 3; i++) {
            PostRequestDto request = PostRequestDto.builder()
                    .title("게시글 " + i)
                    .author("작성자 " + i)
                    .content("내용 " + i)
                    .password("pass" + i)
                    .build();
            postController.createPost(request);
            
            // 시간 차이를 위한 짧은 대기
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        }

        // When
        var allPosts = postController.getAllPosts();

        // Then
        assertThat(allPosts).hasSizeGreaterThanOrEqualTo(4); // testPost + 3개 추가
        
        // 날짜 내림차순 정렬 확인
        for (int i = 0; i < allPosts.size() - 1; i++) {
            assertThat(allPosts.get(i).getWriteDate())
                    .isAfterOrEqualTo(allPosts.get(i + 1).getWriteDate());
        }
        
        log.info("전체 게시글 조회 및 정렬 확인 완료 - 총 {}개", allPosts.size());
    }
}
