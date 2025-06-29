# TDD (Test-Driven Development) 가이드

## 📖 목차
1. [TDD란 무엇인가?](#tdd란-무엇인가)
2. [TDD의 핵심 원리](#tdd의-핵심-원리)
3. [TDD의 장점](#tdd의-장점)
4. [TDD 사이클: Red-Green-Refactor](#tdd-사이클-red-green-refactor)
5. [현재 프로젝트에서의 TDD 적용](#현재-프로젝트에서의-tdd-적용)
6. [Spring Boot에서의 TDD 구현](#spring-boot에서의-tdd-구현)
7. [테스트 종류와 전략](#테스트-종류와-전략)
8. [실무 적용 시 고려사항](#실무-적용-시-고려사항)
9. [결론](#결론)

---

## TDD란 무엇인가?

**TDD(Test-Driven Development)**는 테스트가 개발을 주도하는 소프트웨어 개발 방법론입니다. 
기존의 "코드 작성 → 테스트 작성" 순서와 달리, **"테스트 작성 → 코드 작성"** 순서로 개발을 진행합니다.

### 기본 개념
- **테스트 우선**: 기능을 구현하기 전에 먼저 테스트를 작성
- **최소 구현**: 테스트를 통과할 수 있는 최소한의 코드만 작성
- **지속적 리팩토링**: 테스트를 유지하면서 코드 품질을 개선

---

## TDD의 핵심 원리

### 1. 작은 단위로 개발
```java
// ❌ 나쁜 예: 한 번에 많은 기능을 테스트
@Test
void shouldCreatePostAndValidateAndSaveAndReturn() {
    // 너무 많은 책임을 한 번에 테스트
}

// ✅ 좋은 예: 하나의 기능만 테스트
@Test
void shouldCreatePostWithValidTitle() {
    // 제목 검증만 테스트
}

@Test
void shouldSavePostToRepository() {
    // 저장 기능만 테스트
}
```

### 2. 실패하는 테스트부터 시작
```java
@Test
void shouldReturnPostById() {
    // Given
    Long postId = 1L;
    
    // When & Then
    assertThrows(PostNotFoundException.class, () -> {
        postService.getPostById(postId);
    });
    // 아직 getPostById 메서드가 구현되지 않아 컴파일 에러 발생
}
```

### 3. 테스트를 통과하는 최소 코드 작성
```java
// 최소한의 구현으로 테스트 통과
public Post getPostById(Long id) {
    throw new PostNotFoundException("Post not found");
}
```

---

## TDD의 장점

### 1. 🛡️ 버그 예방
- **조기 발견**: 개발 단계에서 버그를 미리 발견
- **회귀 방지**: 기존 기능이 새로운 변경으로 인해 깨지는 것을 방지

```java
@Test
void shouldNotAllowEmptyTitle() {
    // Given
    PostRequestDto invalidRequest = new PostRequestDto("", "author", "content", "password");
    
    // When & Then
    assertThrows(ValidationException.class, () -> {
        postService.createPost(invalidRequest);
    });
}
```

### 2. 📋 명확한 요구사항 정의
- **문서화 효과**: 테스트가 요구사항을 명확히 표현
- **의사소통 도구**: 개발자 간 요구사항 공유

```java
@Test
void shouldReturnPostsOrderedByCreatedDateDesc() {
    // Given
    Post oldPost = createPost("Old Post", LocalDateTime.now().minusDays(1));
    Post newPost = createPost("New Post", LocalDateTime.now());
    
    // When
    List<PostResponseDto> posts = postService.getAllPosts();
    
    // Then
    assertThat(posts.get(0).getTitle()).isEqualTo("New Post");
    assertThat(posts.get(1).getTitle()).isEqualTo("Old Post");
}
```

### 3. 🔧 설계 개선
- **느슨한 결합**: 테스트 가능한 코드는 자연스럽게 모듈화됨
- **의존성 주입**: Mock 객체 사용으로 의존성 분리

```java
@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    
    @Mock
    private PostRepository postRepository;
    
    @InjectMocks
    private PostService postService;
    
    @Test
    void shouldSavePostSuccessfully() {
        // Given
        PostRequestDto request = new PostRequestDto("Title", "Author", "Content", "password");
        Post savedPost = new Post("Title", "Author", "Content", "password");
        
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        
        // When
        PostResponseDto result = postService.createPost(request);
        
        // Then
        assertThat(result.getTitle()).isEqualTo("Title");
        verify(postRepository).save(any(Post.class));
    }
}
```

### 4. 🚀 빠른 피드백
- **즉시 확인**: 변경사항의 영향을 즉시 확인 가능
- **자신감 증대**: 리팩토링과 기능 추가에 대한 자신감

### 5. 📚 Living Documentation
- **실행 가능한 문서**: 테스트 코드가 최신 상태의 문서 역할
- **예제 코드**: API 사용법을 보여주는 예제

---

## TDD 사이클: Red-Green-Refactor

### 🔴 Red: 실패하는 테스트 작성
```java
@Test
void shouldUpdatePostTitle() {
    // Given
    Long postId = 1L;
    String newTitle = "Updated Title";
    
    // When
    PostResponseDto result = postService.updatePostTitle(postId, newTitle);
    
    // Then
    assertThat(result.getTitle()).isEqualTo(newTitle);
    // 아직 updatePostTitle 메서드가 없어서 컴파일 에러
}
```

### 🟢 Green: 테스트를 통과하는 최소 코드 작성
```java
public PostResponseDto updatePostTitle(Long postId, String newTitle) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException("Post not found"));
    
    post.updateTitle(newTitle);
    Post savedPost = postRepository.save(post);
    
    return new PostResponseDto(savedPost);
}
```

### 🔵 Refactor: 코드 개선
```java
public PostResponseDto updatePostTitle(Long postId, String newTitle) {
    validateTitle(newTitle);  // 검증 로직 추출
    
    Post post = findPostById(postId);  // 공통 메서드 추출
    post.updateTitle(newTitle);
    
    return saveAndConvert(post);  // 저장 및 변환 로직 추출
}

private void validateTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
        throw new ValidationException("Title cannot be empty");
    }
}

private Post findPostById(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));
}

private PostResponseDto saveAndConvert(Post post) {
    Post savedPost = postRepository.save(post);
    return new PostResponseDto(savedPost);
}
```

---

## 현재 프로젝트에서의 TDD 적용

### 현재 상황 분석
우리 프로젝트는 **게시글 관리 시스템**으로 다음 기능들이 있습니다:
- 게시글 목록 조회
- 게시글 작성
- 게시글 상세 조회

### TDD 적용 예시: 게시글 작성 기능

#### 1단계: 실패하는 테스트 작성
```java
@SpringBootTest
@Transactional
class PostServiceTest {
    
    @Autowired
    private PostService postService;
    
    @Test
    void shouldCreatePostWithValidData() {
        // Given
        PostRequestDto request = new PostRequestDto(
            "테스트 제목",
            "테스트 작성자",
            "테스트 내용",
            "password123"
        );
        
        // When
        PostResponseDto result = postService.createPost(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 제목");
        assertThat(result.getAuthor()).isEqualTo("테스트 작성자");
        assertThat(result.getContent()).isEqualTo("테스트 내용");
        assertThat(result.getCreatedDate()).isNotNull();
    }
}
```

#### 2단계: 최소 구현
```java
@Service
@Transactional
public class PostService {
    
    private final PostRepository postRepository;
    
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    public PostResponseDto createPost(PostRequestDto request) {
        Post post = new Post(
            request.getTitle(),
            request.getAuthor(),
            request.getContent(),
            request.getPassword()
        );
        
        Post savedPost = postRepository.save(post);
        return new PostResponseDto(savedPost);
    }
}
```

#### 3단계: 검증 테스트 추가
```java
@Test
void shouldThrowExceptionWhenTitleIsEmpty() {
    // Given
    PostRequestDto request = new PostRequestDto(
        "",  // 빈 제목
        "작성자",
        "내용",
        "password"
    );
    
    // When & Then
    assertThrows(ValidationException.class, () -> {
        postService.createPost(request);
    });
}

@Test
void shouldThrowExceptionWhenAuthorIsEmpty() {
    // Given
    PostRequestDto request = new PostRequestDto(
        "제목",
        "",  // 빈 작성자
        "내용",
        "password"
    );
    
    // When & Then
    assertThrows(ValidationException.class, () -> {
        postService.createPost(request);
    });
}
```

#### 4단계: 검증 로직 구현
```java
public PostResponseDto createPost(PostRequestDto request) {
    validatePostRequest(request);
    
    Post post = new Post(
        request.getTitle(),
        request.getAuthor(),
        request.getContent(),
        request.getPassword()
    );
    
    Post savedPost = postRepository.save(post);
    return new PostResponseDto(savedPost);
}

private void validatePostRequest(PostRequestDto request) {
    if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
        throw new ValidationException("제목은 필수입니다");
    }
    if (request.getAuthor() == null || request.getAuthor().trim().isEmpty()) {
        throw new ValidationException("작성자는 필수입니다");
    }
    if (request.getContent() == null || request.getContent().trim().isEmpty()) {
        throw new ValidationException("내용은 필수입니다");
    }
    if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
        throw new ValidationException("비밀번호는 필수입니다");
    }
}
```

---

## Spring Boot에서의 TDD 구현

### 1. 단위 테스트 (Unit Test)
```java
@ExtendWith(MockitoExtension.class)
class PostServiceUnitTest {
    
    @Mock
    private PostRepository postRepository;
    
    @InjectMocks
    private PostService postService;
    
    @Test
    void shouldReturnAllPostsOrderedByDate() {
        // Given
        List<Post> posts = Arrays.asList(
            createPost("Post 1", LocalDateTime.now().minusDays(1)),
            createPost("Post 2", LocalDateTime.now())
        );
        when(postRepository.findAllByOrderByCreatedDateDesc()).thenReturn(posts);
        
        // When
        List<PostResponseDto> result = postService.getAllPosts();
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Post 2");
        verify(postRepository).findAllByOrderByCreatedDateDesc();
    }
}
```

### 2. 통합 테스트 (Integration Test)
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class PostServiceIntegrationTest {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private PostRepository postRepository;
    
    @Test
    void shouldPersistPostToDatabase() {
        // Given
        PostRequestDto request = new PostRequestDto(
            "통합 테스트 제목",
            "통합 테스트 작성자",
            "통합 테스트 내용",
            "password"
        );
        
        // When
        PostResponseDto result = postService.createPost(request);
        
        // Then
        assertThat(result.getId()).isNotNull();
        
        Post savedPost = postRepository.findById(result.getId()).orElse(null);
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getTitle()).isEqualTo("통합 테스트 제목");
    }
}
```

### 3. 컨트롤러 테스트
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApiGatewayControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void shouldCreatePostThroughApiGateway() throws Exception {
        // Given
        PostRequestDto request = new PostRequestDto(
            "API 테스트 제목",
            "API 테스트 작성자",
            "API 테스트 내용",
            "password"
        );
        
        // When & Then
        mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("API 테스트 제목"))
                .andExpect(jsonPath("$.author").value("API 테스트 작성자"))
                .andExpect(jsonPath("$.id").exists());
    }
    
    @Test
    void shouldReturnAllPostsInDescendingOrder() throws Exception {
        // Given
        // 테스트 데이터 생성
        createTestPost("첫 번째 글", LocalDateTime.now().minusDays(1));
        createTestPost("두 번째 글", LocalDateTime.now());
        
        // When & Then
        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("두 번째 글"))
                .andExpect(jsonPath("$[1].title").value("첫 번째 글"));
    }
}
```

---

## 테스트 종류와 전략

### 1. 테스트 피라미드
```
    /\
   /  \     E2E Tests (소수)
  /____\    
 /      \   Integration Tests (적당량)
/__________\ Unit Tests (대다수)
```

### 2. 각 테스트 종류별 특징

#### Unit Test (단위 테스트)
- **목적**: 개별 메서드나 클래스의 기능 검증
- **특징**: 빠름, 독립적, Mock 사용
- **예시**: PostService.createPost() 메서드 테스트

#### Integration Test (통합 테스트)
- **목적**: 여러 컴포넌트 간의 상호작용 검증
- **특징**: 실제 데이터베이스 사용, 느림
- **예시**: PostService + PostRepository 연동 테스트

#### E2E Test (종단간 테스트)
- **목적**: 전체 시스템의 동작 검증
- **특징**: 실제 환경과 유사, 가장 느림
- **예시**: HTTP 요청부터 데이터베이스 저장까지 전 과정

### 3. 테스트 전략

#### Given-When-Then 패턴
```java
@Test
void shouldCalculatePostStatistics() {
    // Given (준비)
    List<Post> posts = createTestPosts();
    
    // When (실행)
    PostStatistics stats = postService.calculateStatistics(posts);
    
    // Then (검증)
    assertThat(stats.getTotalCount()).isEqualTo(3);
    assertThat(stats.getAverageContentLength()).isEqualTo(150.0);
}
```

#### 경계값 테스트
```java
@Test
void shouldHandleEmptyPostList() {
    // Given
    List<Post> emptyList = Collections.emptyList();
    
    // When
    PostStatistics stats = postService.calculateStatistics(emptyList);
    
    // Then
    assertThat(stats.getTotalCount()).isZero();
    assertThat(stats.getAverageContentLength()).isZero();
}

@Test
void shouldHandleMaximumTitleLength() {
    // Given
    String maxLengthTitle = "a".repeat(255);  // 최대 길이
    PostRequestDto request = new PostRequestDto(maxLengthTitle, "author", "content", "password");
    
    // When & Then
    assertDoesNotThrow(() -> {
        postService.createPost(request);
    });
}

@Test
void shouldRejectTitleExceedingMaxLength() {
    // Given
    String tooLongTitle = "a".repeat(256);  // 최대 길이 초과
    PostRequestDto request = new PostRequestDto(tooLongTitle, "author", "content", "password");
    
    // When & Then
    assertThrows(ValidationException.class, () -> {
        postService.createPost(request);
    });
}
```

---

## 실무 적용 시 고려사항

### 1. TDD 도입 시점
- **신규 프로젝트**: 처음부터 TDD 적용 권장
- **기존 프로젝트**: 점진적 도입 (새로운 기능부터 시작)

### 2. 팀 협업
```java
// 팀 컨벤션 예시
@DisplayName("게시글 서비스 테스트")
class PostServiceTest {
    
    @DisplayName("유효한 데이터로 게시글을 생성할 수 있다")
    @Test
    void shouldCreatePostWithValidData() {
        // 한국어로 명확한 테스트 의도 표현
    }
    
    @DisplayName("제목이 비어있으면 예외가 발생한다")
    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        // 예외 상황도 명확히 표현
    }
}
```

### 3. 테스트 데이터 관리
```java
@TestConfiguration
public class TestDataConfig {
    
    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
    }
}

// 테스트 헬퍼 클래스
public class PostTestHelper {
    
    public static PostRequestDto createValidPostRequest() {
        return new PostRequestDto(
            "테스트 제목",
            "테스트 작성자",
            "테스트 내용",
            "password123"
        );
    }
    
    public static PostRequestDto createInvalidPostRequest() {
        return new PostRequestDto("", "", "", "");
    }
}
```

### 4. 성능 고려사항
```java
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false",  // 테스트 시 SQL 로그 비활성화
    "logging.level.org.springframework.web=WARN"
})
class FastIntegrationTest {
    // 빠른 테스트 실행을 위한 설정
}
```

### 5. CI/CD 연동
```yaml
# .github/workflows/test.yml
name: Test
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run tests
        run: ./gradlew test
      - name: Generate test report
        uses: dorny/test-reporter@v1
        if: success() || failure()
        with:
          name: Test Results
          path: build/test-results/test/*.xml
          reporter: java-junit
```

---

## 결론

### TDD의 핵심 가치
1. **품질 향상**: 버그 감소와 코드 품질 개선
2. **설계 개선**: 테스트 가능한 코드는 좋은 설계
3. **문서화**: 실행 가능한 명세서 역할
4. **자신감**: 변경에 대한 두려움 제거

### 현재 프로젝트에서의 적용 방안
1. **단계적 도입**: 새로운 기능부터 TDD 적용
2. **기존 코드 개선**: 레거시 코드에 테스트 추가
3. **팀 문화 구축**: 코드 리뷰에 테스트 포함

### 성공적인 TDD를 위한 조언
- **작게 시작하기**: 복잡한 기능도 작은 단위로 분해
- **실패를 두려워하지 말기**: 실패하는 테스트가 시작점
- **지속적인 리팩토링**: 테스트가 있기에 안전한 리팩토링 가능
- **팀과 함께하기**: TDD는 개인이 아닌 팀의 문화

---

> **"TDD는 단순히 테스트를 작성하는 것이 아니라, 더 나은 소프트웨어를 만들기 위한 사고방식의 전환입니다."**

---

**문서 버전**: 1.0  
**작성일**: 2024년  
**프로젝트**: study-learn-spring-posts  
**작성자**: AI Assistant 