package com.example.studylearnspringposts.controller;

import com.example.studylearnspringposts.dto.PostRequestDto;
import com.example.studylearnspringposts.dto.PostResponseDto;
import com.example.studylearnspringposts.exception.PostNotFoundException;
import com.example.studylearnspringposts.service.PostService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional // 테스트 격리를 위해 붙여주는 어노테이션 > 여러개 테스트돌린후 데이터 자동으로 지울 수 있다고함 : 필수인지는 모르겠음
class PostControllerTest {
    private static final Logger log = LoggerFactory.getLogger(PostControllerTest.class);

    // 실제 통합 테스트를 위해 @Autowired 사용 (MockitoBean 대신)
    @MockBean
    PostService postService;

    @Autowired
    PostController postController;
    
    // BeforeEach에서 생성한 게시글을 테스트에서 사용할 수 있도록 저장
    private PostResponseDto createdPost;

    @BeforeAll
    static void beforeAll(){
        log.info("[항해 99] : Before All 단계 호출(최초시작)");
        log.info("[항해 99] : 이러한 단계에서 데이터나 공통으로 사용되는 코드들을 세팅");
        log.info("[항해 99] : 각 어노테이션들의 순서");
        log.info("[항해 99] : 1. BeforeAll");
        log.info("[항해 99] : 2. beforeEach");
        log.info("[항해 99] : 3. @Test 1 시작");
        log.info("[항해 99] : 4. afterEach");
        log.info("[항해 99] : 5. beforeEach");
        log.info("[항해 99] : 6. @Test 2 시작");
        log.info("[항해 99] : 7. afterEach");
        log.info("[항해 99] : 8. afterAll(최종종료)");
    }

    @BeforeEach
    void beforeEach() {
        // 예를들면 여러테스트에서 필요한 재료가 중복된다면 여기에 사용가능함.
        log.info("[항해 99] : BeforeEach 단계 - 테스트용 게시글 생성");
        
        PostRequestDto createRequest = new PostRequestDto(
            "테스트용게시글","작성자","내용","pass123123"
        ); // 테스트하려면 어쨋든 객체가 가장먼저 만들어져 있긴 해야할듯

        createdPost = postController.createPost(createRequest);
        log.info("[항해 99] : 생성된 게시글 ID: {}", createdPost.getId());
    }

    // 실패하는 테스트를 만들어보자 - 현재는 성공하는 조회 테스트로 변경
    @Test
    void shouldGetPostById_Success() {
        log.info("[항해 99] : 게시글 조회 테스트 시작");
        
        // given 준비단계 : BeforeEach에서 생성된 게시글 사용
        Long postId = createdPost.getId();
        log.info("[항해 99] : 조회할 게시글 ID: {}", postId);

        // when 실행단계 : 게시글 조회
        PostResponseDto foundPost = postController.getPostById(postId);

        // Then 검증단계 :
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getId()).isEqualTo(postId);
        assertThat(foundPost.getTitle()).isEqualTo("테스트용게시글");
        assertThat(foundPost.getAuthor()).isEqualTo("작성자");
        
        log.info("[항해 99] : 게시글 조회 테스트 성공");
    }
    
    @Test
    void shouldThrowException_WhenPostNotFound() {
        log.info("[항해 99] : 존재하지 않는 게시글 조회 테스트 시작");
        
        // given 준비단계 : 존재하지 않는 ID
        Long nonExistentId = 999999L;

        // when & then 실행 및 검증단계 : 예외 발생 확인
        assertThrows(PostNotFoundException.class, () -> {
            postController.getPostById(nonExistentId);
        });
        
        log.info("[항해 99] : 존재하지 않는 게시글 조회시 예외 발생 확인 완료");
    }
    
    @Test
    void shouldCreatePost_Success() {
        log.info("[항해 99] : 게시글 생성 테스트 시작");
        
        // given 준비단계 : 새로운 게시글 요청 데이터
        PostRequestDto newPostRequest = new PostRequestDto(
            "새로운게시글", "새작성자", "새내용", "newpass123"
        );

        // when 실행단계 : 게시글 생성
        PostResponseDto newPost = postController.createPost(newPostRequest);

        // then 검증단계 :
        assertThat(newPost).isNotNull();
        assertThat(newPost.getId()).isNotNull();
        assertThat(newPost.getTitle()).isEqualTo("새로운게시글");
        assertThat(newPost.getAuthor()).isEqualTo("새작성자");
        assertThat(newPost.getContent()).isEqualTo("새내용");
        
        log.info("[항해 99] : 게시글 생성 테스트 성공 - ID: {}", newPost.getId());
    }
    
    @Test 
    void shouldGetAllPosts_Success() {
        log.info("[항해 99] : 전체 게시글 조회 테스트 시작");
        
        // given 준비단계 : BeforeEach에서 이미 하나의 게시글이 생성됨
        // 추가로 하나 더 생성
        PostRequestDto additionalRequest = new PostRequestDto(
            "추가게시글", "추가작성자", "추가내용", "addpass123"
        );
        postController.createPost(additionalRequest);

        // when 실행단계 : 전체 게시글 조회
        var allPosts = postController.getAllPosts();

        // then 검증단계 :
        assertThat(allPosts).isNotNull();
        assertThat(allPosts).hasSizeGreaterThanOrEqualTo(2); // 최소 2개 이상
        
        log.info("[항해 99] : 전체 게시글 조회 테스트 성공 - 총 {}개", allPosts.size());
    }
    
    // ===== 수정 API 테스트들 =====
    
    @Test
    void shouldUpdatePost_Success() {
        log.info("[항해 99] : 게시글 수정 테스트 시작");
        
        // given 준비단계 : BeforeEach에서 생성된 게시글 사용
        Long postId = createdPost.getId();
        String correctPassword = "pass123123"; // BeforeEach에서 설정한 비밀번호
        PostRequestDto updateRequest = new PostRequestDto(
            "수정된제목", "수정된작성자", "수정된내용", correctPassword
        );
        log.info("[항해 99] : 수정할 게시글 ID: {}", postId);

        // when 실행단계 : 게시글 수정 (아직 구현되지 않은 메서드!)
        PostResponseDto updatedPost = postController.updatePost(postId, updateRequest);

        // then 검증단계 :
        assertThat(updatedPost).isNotNull();
        assertThat(updatedPost.getId()).isEqualTo(postId);
        assertThat(updatedPost.getTitle()).isEqualTo("수정된제목");
        assertThat(updatedPost.getAuthor()).isEqualTo("수정된작성자");
        assertThat(updatedPost.getContent()).isEqualTo("수정된내용");
        
        log.info("[항해 99] : 게시글 수정 테스트 성공");
    }
    
    @Test
    void shouldFailUpdatePost_WhenWrongPassword() {
        log.info("[항해 99] : 잘못된 비밀번호로 게시글 수정 테스트 시작");
        
        // given 준비단계 : BeforeEach에서 생성된 게시글 + 잘못된 비밀번호
        Long postId = createdPost.getId();
        String wrongPassword = "wrongpass123"; // 잘못된 비밀번호
        PostRequestDto updateRequest = new PostRequestDto(
            "수정된제목", "수정된작성자", "수정된내용", wrongPassword
        );

        // when & then 실행 및 검증단계 : 예외 발생 확인
        assertThrows(IllegalArgumentException.class, () -> {
            postController.updatePost(postId, updateRequest);
        });
        
        log.info("[항해 99] : 잘못된 비밀번호로 수정시 예외 발생 확인 완료");
    }
    
    @Test
    void shouldFailUpdatePost_WhenPostNotFound() {
        log.info("[항해 99] : 존재하지 않는 게시글 수정 테스트 시작");
        
        // given 준비단계 : 존재하지 않는 게시글 ID
        Long nonExistentId = 999999L;
        PostRequestDto updateRequest = new PostRequestDto(
            "수정된제목", "수정된작성자", "수정된내용", "anypassword"
        );

        // when & then 실행 및 검증단계 : 예외 발생 확인
        assertThrows(PostNotFoundException.class, () -> {
            postController.updatePost(nonExistentId, updateRequest);
        });
        
        log.info("[항해 99] : 존재하지 않는 게시글 수정시 예외 발생 확인 완료");
    }
    
    @Test
    void shouldUpdateOnlyTitle_PartialUpdate() {
        log.info("[항해 99] : 제목만 수정하는 테스트 시작");
        
        // given 준비단계 : 제목만 변경하는 요청
        Long postId = createdPost.getId();
        String correctPassword = "pass123123";
        PostRequestDto updateRequest = new PostRequestDto(
            "제목만수정", "작성자", "내용", correctPassword // 제목만 변경
        );

        // when 실행단계 : 게시글 수정
        PostResponseDto updatedPost = postController.updatePost(postId, updateRequest);

        // then 검증단계 :
        assertThat(updatedPost.getTitle()).isEqualTo("제목만수정");
        assertThat(updatedPost.getAuthor()).isEqualTo("작성자"); // 기존과 동일
        assertThat(updatedPost.getContent()).isEqualTo("내용"); // 기존과 동일
        
        log.info("[항해 99] : 제목만 수정 테스트 성공");
    }
}
