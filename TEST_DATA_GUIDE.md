# 테스트 데이터 처리 가이드

## 📖 목차
1. [테스트 데이터의 중요성](#테스트-데이터의-중요성)
2. [테스트 데이터 처리 전략](#테스트-데이터-처리-전략)
3. [Spring Boot 테스트 환경 설정](#spring-boot-테스트-환경-설정)
4. [테스트 데이터 격리 방법](#테스트-데이터-격리-방법)
5. [테스트 데이터 생성 패턴](#테스트-데이터-생성-패턴)
6. [실무 적용 사례](#실무-적용-사례)
7. [베스트 프랙티스](#베스트-프랙티스)

---

## 테스트 데이터의 중요성

### 왜 테스트 데이터 관리가 중요한가?

1. **테스트 격리**: 각 테스트가 독립적으로 실행되어야 함
2. **재현 가능성**: 언제든지 동일한 결과를 얻을 수 있어야 함
3. **신뢰성**: 테스트가 실패하면 코드 문제임을 확신할 수 있어야 함
4. **속도**: 테스트 데이터 설정과 정리가 빨라야 함

---

## 테스트 데이터 처리 전략

### 현재 프로젝트 환경 분석

우리 프로젝트의 현재 설정:
```properties
# H2 인메모리 데이터베이스
spring.datasource.url=jdbc:h2:mem:db
spring.jpa.hibernate.ddl-auto=create-drop  # 테스트에 이상적
spring.sql.init.mode=never                  # 수동 데이터 관리
```

이 설정은 테스트에 **매우 적합**합니다! 🎯

### 1. 테스트 환경별 데이터베이스 전략

#### 🏃‍♂️ 단위 테스트 (Unit Test)
```java
@ExtendWith(MockitoExtension.class)
class PostServiceUnitTest {
    
    @Mock
    private PostRepository postRepository;
    
    @InjectMocks
    private PostService postService;
    
    @Test
    void shouldCreatePost() {
        // Given - Mock 데이터 사용 (실제 DB 없음)
        PostRequestDto request = new PostRequestDto("제목", "작성자", "내용", "password");
        Post savedPost = new Post("제목", "작성자", "내용", "password");
        savedPost.setId(1L);
        
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        
        // When
        PostResponseDto result = postService.createPost(request);
        
        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("제목");
    }
}
```

#### 🔄 통합 테스트 (Integration Test)
```java
@SpringBootTest
@Transactional  // 각 테스트 후 자동 롤백! 중요!
class PostServiceIntegrationTest {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private PostRepository postRepository;
    
    @Test
    void shouldSaveAndRetrievePost() {
        // Given - H2 인메모리 DB 사용
        PostRequestDto request = new PostRequestDto(
            "통합 테스트 제목",
            "테스트 작성자", 
            "테스트 내용",
            "password"
        );
        
        // When
        PostResponseDto createdPost = postService.createPost(request);
        
        // Then
        Optional<Post> foundPost = postRepository.findById(createdPost.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("통합 테스트 제목");
        // @Transactional로 테스트 완료 후 자동 롤백됨
    }
}
```

---

## Spring Boot 테스트 환경 설정

### 1. 테스트 전용 프로파일

#### `src/test/resources/application-test.properties`
```properties
# 테스트 전용 H2 설정
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop

# 성능 향상을 위한 설정
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
spring.sql.init.mode=never

# 로깅 최적화
logging.level.org.springframework.web=WARN
logging.level.com.example.studylearnspringposts=INFO
```

### 2. 기본 테스트 클래스

```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {
    
    @Autowired
    protected PostService postService;
    
    @Autowired
    protected PostRepository postRepository;
    
    // 공통 테스트 헬퍼 메서드들
    protected Post createAndSavePost(String title) {
        Post post = new Post(title, "테스트 작성자", "테스트 내용", "password");
        return postRepository.save(post);
    }
    
    protected List<Post> createMultiplePosts(int count) {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            posts.add(new Post("제목 " + i, "작성자 " + i, "내용 " + i, "password" + i));
        }
        return postRepository.saveAll(posts);
    }
}
```

---

## 테스트 데이터 격리 방법

### 1. @Transactional 활용 (가장 권장!)

```java
@SpringBootTest
@Transactional  // ⭐ 핵심! 테스트 후 자동 롤백
class PostServiceTransactionalTest {
    
    @Autowired
    private PostService postService;
    
    @Test
    void test1_CreatePost() {
        // Given
        PostRequestDto request = new PostRequestDto("제목1", "작성자1", "내용1", "password");
        
        // When
        PostResponseDto result = postService.createPost(request);
        
        // Then
        assertThat(result.getId()).isNotNull();
        // 테스트 종료 후 자동으로 롤백됨 ✨
    }
    
    @Test
    void test2_ShouldBeClean() {
        // Given - 이전 테스트 데이터가 없어야 함
        List<PostResponseDto> posts = postService.getAllPosts();
        
        // Then
        assertThat(posts).isEmpty(); // ✅ 깔끔하게 비어있음
    }
}
```

### 2. @DirtiesContext 활용 (필요시)

```java
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PostServiceDirtiesContextTest {
    // Spring 컨텍스트를 매번 새로 만듦 (무겁지만 확실함)
    
    @Test
    void shouldCreatePost() {
        // 테스트 코드...
    }
}
```

### 3. @Sql 어노테이션 활용

```java
@SpringBootTest
@Transactional
class PostServiceSqlTest {
    
    @Test
    @Sql("/test-data/posts-setup.sql")  // 테스트 전 실행
    @Sql(scripts = "/test-data/cleanup.sql", 
         executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)  // 테스트 후 실행
    void shouldFindExistingPosts() {
        List<PostResponseDto> posts = postService.getAllPosts();
        assertThat(posts).isNotEmpty();
    }
}
```

---

## 테스트 데이터 생성 패턴

### 1. Builder 패턴 (매우 유용!)

```java
public class PostTestDataBuilder {
    private String title = "기본 제목";
    private String author = "기본 작성자";
    private String content = "기본 내용";
    private String password = "password123";
    private LocalDateTime createdDate = LocalDateTime.now();
    
    public static PostTestDataBuilder aPost() {
        return new PostTestDataBuilder();
    }
    
    public PostTestDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public PostTestDataBuilder withAuthor(String author) {
        this.author = author;
        return this;
    }
    
    public PostTestDataBuilder withContent(String content) {
        this.content = content;
        return this;
    }
    
    public PostTestDataBuilder createdAt(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }
    
    public Post build() {
        Post post = new Post(title, author, content, password);
        post.setCreatedDate(createdDate);
        return post;
    }
    
    public PostRequestDto buildRequest() {
        return new PostRequestDto(title, author, content, password);
    }
}

// 💡 사용 예시
@Test
void shouldCreateDifferentPosts() {
    // Given - 매우 읽기 쉽고 유연함
    Post oldPost = PostTestDataBuilder.aPost()
        .withTitle("오래된 글")
        .createdAt(LocalDateTime.now().minusDays(1))
        .build();
        
    Post newPost = PostTestDataBuilder.aPost()
        .withTitle("새로운 글")
        .withAuthor("새 작성자")
        .build();
        
    // When & Then
    assertThat(oldPost.getTitle()).isEqualTo("오래된 글");
    assertThat(newPost.getAuthor()).isEqualTo("새 작성자");
}
```

### 2. Factory 패턴

```java
@Component
public class PostTestDataFactory {
    
    public Post createValidPost() {
        return new Post("테스트 제목", "테스트 작성자", "테스트 내용", "password123");
    }
    
    public List<Post> createMultiplePosts(int count) {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            posts.add(new Post(
                "제목 " + i,
                "작성자 " + i,
                "내용 " + i,
                "password" + i
            ));
        }
        return posts;
    }
    
    public List<Post> createPostsForDateSorting() {
        LocalDateTime now = LocalDateTime.now();
        return Arrays.asList(
            createPostWithDate(now.minusDays(2)),
            createPostWithDate(now.minusDays(1)),
            createPostWithDate(now)
        );
    }
    
    private Post createPostWithDate(LocalDateTime date) {
        Post post = createValidPost();
        post.setCreatedDate(date);
        return post;
    }
}
```

### 3. 픽스처 클래스 (상수 관리)

```java
public class PostFixtures {
    
    public static final String DEFAULT_TITLE = "기본 제목";
    public static final String DEFAULT_AUTHOR = "기본 작성자";
    public static final String DEFAULT_CONTENT = "기본 내용";
    public static final String DEFAULT_PASSWORD = "password123";
    
    public static Post defaultPost() {
        return new Post(DEFAULT_TITLE, DEFAULT_AUTHOR, DEFAULT_CONTENT, DEFAULT_PASSWORD);
    }
    
    public static PostRequestDto defaultPostRequest() {
        return new PostRequestDto(DEFAULT_TITLE, DEFAULT_AUTHOR, DEFAULT_CONTENT, DEFAULT_PASSWORD);
    }
    
    public static Post emptyTitlePost() {
        return new Post("", DEFAULT_AUTHOR, DEFAULT_CONTENT, DEFAULT_PASSWORD);
    }
    
    // 경계값 테스트용
    public static Post maxLengthTitlePost() {
        String maxTitle = "가".repeat(255);
        return new Post(maxTitle, DEFAULT_AUTHOR, DEFAULT_CONTENT, DEFAULT_PASSWORD);
    }
}
```

---

## 실무 적용 사례

### 1. 현재 프로젝트 테스트 개선

현재 `MainControllerTest.java`를 개선해보겠습니다:

#### 기존 (문제점)
```java
class MainControllerTest {
    @Test
    void test() {
        log.info("test");  // 의미 없는 테스트
    }
}
```

#### 개선된 버전
```java
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MainControllerImprovedTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private PostRepository postRepository;
    
    @Test
    @DisplayName("게시글 목록을 날짜 내림차순으로 조회한다")
    void shouldReturnPostsOrderedByDateDesc() throws Exception {
        // Given - 명확한 시간 차이로 테스트 데이터 생성
        LocalDateTime now = LocalDateTime.now();
        Post oldPost = PostTestDataBuilder.aPost()
            .withTitle("오래된 게시글")
            .createdAt(now.minusDays(1))
            .build();
        Post newPost = PostTestDataBuilder.aPost()
            .withTitle("최신 게시글")
            .createdAt(now)
            .build();
        
        postRepository.saveAll(Arrays.asList(oldPost, newPost));
        
        // When & Then
        mockMvc.perform(get("/board"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].title", is("최신 게시글")))
            .andExpect(jsonPath("$[1].title", is("오래된 게시글")));
    }
    
    @Test
    @DisplayName("유효한 게시글을 생성한다")
    void shouldCreateValidPost() throws Exception {
        // Given
        PostRequestDto request = PostTestDataBuilder.aPost()
            .withTitle("새 게시글")
            .withAuthor("홍길동")
            .buildRequest();
        
        // When & Then
        mockMvc.perform(post("/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title", is("새 게시글")))
            .andExpect(jsonPath("$.author", is("홍길동")))
            .andExpect(jsonPath("$.id", notNullValue()));
    }
    
    @Test
    @DisplayName("빈 제목으로 게시글 생성 시 400 에러가 발생한다")
    void shouldReturn400WhenTitleIsEmpty() throws Exception {
        // Given
        PostRequestDto invalidRequest = new PostRequestDto("", "작성자", "내용", "password");
        
        // When & Then
        mockMvc.perform(post("/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }
}
```

### 2. 페이징 테스트

```java
@Test
@DisplayName("페이징이 정상 작동한다")
void shouldPaginateCorrectly() {
    // Given - 대량 데이터 생성
    List<Post> posts = IntStream.range(1, 26) // 25개
        .mapToObj(i -> PostTestDataBuilder.aPost()
            .withTitle("제목 " + String.format("%02d", i))
            .createdAt(LocalDateTime.now().minusDays(i))
            .build())
        .collect(Collectors.toList());
    
    postRepository.saveAll(posts);
    
    // When
    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
    Page<Post> page = postRepository.findAll(pageable);
    
    // Then
    assertThat(page.getContent()).hasSize(10);
    assertThat(page.getTotalElements()).isEqualTo(25);
    assertThat(page.getTotalPages()).isEqualTo(3);
    
    // 최신 게시글이 첫 번째인지 확인
    assertThat(page.getContent().get(0).getTitle()).isEqualTo("제목 01");
}
```

### 3. 동시성 테스트

```java
@Test
@DisplayName("동시에 게시글을 생성해도 데이터 무결성이 보장된다")
void shouldHandleConcurrentCreation() throws InterruptedException {
    // Given
    int threadCount = 10;
    CountDownLatch latch = new CountDownLatch(threadCount);
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    List<PostResponseDto> results = new CopyOnWriteArrayList<>();
    
    // When
    for (int i = 0; i < threadCount; i++) {
        final int index = i;
        executor.submit(() -> {
            try {
                PostRequestDto request = PostTestDataBuilder.aPost()
                    .withTitle("동시성 테스트 " + index)
                    .buildRequest();
                
                PostResponseDto result = postService.createPost(request);
                results.add(result);
            } finally {
                latch.countDown();
            }
        });
    }
    
    latch.await(10, TimeUnit.SECONDS);
    executor.shutdown();
    
    // Then
    assertThat(results).hasSize(threadCount);
    Set<Long> uniqueIds = results.stream()
        .map(PostResponseDto::getId)
        .collect(Collectors.toSet());
    assertThat(uniqueIds).hasSize(threadCount); // 모든 ID가 유니크
}
```

---

## 베스트 프랙티스

### ✅ DO (권장사항)

#### 1. 명확하고 의미 있는 테스트 데이터
```java
@Test
void shouldSortPostsByDate() {
    // ✅ 명확한 시간 차이
    LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
    Post oldPost = PostTestDataBuilder.aPost()
        .withTitle("오래된 글")
        .createdAt(baseTime.minusDays(1))
        .build();
    Post newPost = PostTestDataBuilder.aPost()
        .withTitle("새로운 글")
        .createdAt(baseTime)
        .build();
        
    // 테스트가 의도를 명확히 보여줌
}
```

#### 2. 경계값 정확히 테스트
```java
@Test
void shouldValidateTitleLength() {
    // ✅ 정확한 경계값 테스트
    String validTitle = "가".repeat(255);      // 최대 길이
    String invalidTitle = "가".repeat(256);    // 초과 길이
    
    PostRequestDto validRequest = PostTestDataBuilder.aPost()
        .withTitle(validTitle)
        .buildRequest();
    PostRequestDto invalidRequest = PostTestDataBuilder.aPost()
        .withTitle(invalidTitle)
        .buildRequest();
    
    assertDoesNotThrow(() -> postService.createPost(validRequest));
    assertThrows(ValidationException.class, () -> postService.createPost(invalidRequest));
}
```

#### 3. 독립적인 테스트
```java
@Test
void shouldCreateUniquePost() {
    // ✅ 다른 테스트와 독립적
    String uniqueTitle = "테스트 제목 " + UUID.randomUUID();
    PostRequestDto request = PostTestDataBuilder.aPost()
        .withTitle(uniqueTitle)
        .buildRequest();
    
    PostResponseDto result = postService.createPost(request);
    assertThat(result.getTitle()).isEqualTo(uniqueTitle);
}
```

### ❌ DON'T (피해야 할 사항)

#### 1. 하드코딩된 ID 의존 (절대 금지!)
```java
@Test
void badExample_HardcodedId() {
    // ❌ 특정 ID가 항상 존재한다고 가정 - 매우 위험!
    PostResponseDto post = postService.getPost(1L);
    assertThat(post).isNotNull();
}
```

#### 2. 테스트 간 의존성 (절대 금지!)
```java
@TestMethodOrder(OrderAnnotation.class)
class BadTestExample {
    
    @Test
    @Order(1)
    void badExample_CreateData() {
        // ❌ 다음 테스트를 위한 데이터 생성
        postService.createPost(PostFixtures.defaultPostRequest());
    }
    
    @Test
    @Order(2)
    void badExample_UseCreatedData() {
        // ❌ 이전 테스트에 의존 - 매우 위험!
        List<PostResponseDto> posts = postService.getAllPosts();
        assertThat(posts).hasSize(1);
    }
}
```

#### 3. 과도한 테스트 데이터
```java
@Test
void badExample_TooMuchData() {
    // ❌ 불필요하게 많은 데이터 - 느리고 의미없음
    List<Post> posts = dataFactory.createMultiplePosts(10000);
    postRepository.saveAll(posts);
    
    // 실제로는 하나만 필요함
    PostResponseDto found = postService.getPost(posts.get(0).getId());
    assertThat(found).isNotNull();
}
```

### 🧹 테스트 정리 체크리스트

#### 필수 확인사항
- [ ] `@Transactional` 어노테이션 적용됨
- [ ] 테스트 데이터가 다른 테스트에 영향 주지 않음
- [ ] 하드코딩된 ID 사용하지 않음
- [ ] 테스트별로 필요한 최소한의 데이터만 생성
- [ ] 의미 있는 테스트 데이터 (현실적인 값)

#### 권장 설정
```java
@SpringBootTest
@Transactional          // 🔥 필수! 자동 롤백
@ActiveProfiles("test") // 🔥 필수! 테스트 전용 설정
@DisplayName("게시글 서비스 테스트")
class PostServiceTest {
    // 테스트 코드...
}
```

---

## 요약

### 🎯 핵심 포인트

1. **H2 + @Transactional = 완벽한 조합** 
   - 빠르고 격리된 테스트 환경

2. **Builder 패턴으로 유연한 데이터 생성**
   - 읽기 쉽고 유지보수하기 좋음

3. **픽스처로 공통 데이터 관리**
   - 중복 제거와 일관성 보장

4. **각 테스트는 독립적이어야 함**
   - 순서나 다른 테스트에 의존하면 안됨

### 🚀 현재 프로젝트 적용 계획

1. **MainControllerTest 리팩토링** - 의미 있는 테스트로 변경
2. **PostService 테스트 추가** - TDD 방식으로 작성
3. **Builder 패턴 도입** - 테스트 데이터 생성 표준화
4. **통합 테스트 강화** - API Gateway 패턴 검증

---

> **"좋은 테스트는 좋은 테스트 데이터에서 시작됩니다!"** 

---

**문서 버전**: 1.0  
**작성일**: 2024년  
**프로젝트**: study-learn-spring-posts  
**관련 문서**: [TDD_GUIDE.md](./TDD_GUIDE.md)
