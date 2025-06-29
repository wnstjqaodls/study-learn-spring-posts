# 🎬 Spring Boot 게시판 프로젝트 시연 가이드

## 🎯 **시연 목표**
> **"실제 동작하는 API와 완벽한 통합 테스트를 라이브로 보여주기"**

---

## 📋 **시연 준비사항**

### **필요한 도구들**
1. ✅ **IntelliJ IDEA** (프로젝트 열기)
2. ✅ **웹 브라우저** (Chrome/Edge 권장)
3. ✅ **Postman** (또는 Thunder Client, curl)
4. ✅ **터미널** (Gradle 명령어용)

### **사전 준비**
```bash
# 1. JDK 17 환경변수 설정 확인
$env:JAVA_HOME="C:\Users\{username}\.jdks\jbr-17.0.14"

# 2. 프로젝트 빌드 확인
./gradlew clean build
```

---

## 🚀 **시연 시나리오 (15분 완벽 가이드)**

### **1단계: 프로젝트 소개 (2분)**

#### **IntelliJ에서 보여줄 것들**
```
📁 프로젝트 구조 설명
├── src/main/java/
│   ├── controller/
│   │   ├── ApiGatewayController.java ← "API 진입점"
│   │   └── PostController.java ← "비즈니스 로직"
│   ├── service/PostService.java ← "서비스 계층"
│   ├── repository/PostRepository.java ← "데이터 계층"
│   └── exception/GlobalExceptionHandler.java ← "예외 처리"
└── src/test/java/
    └── controller/
        ├── PostControllerIntegrationTest.java ← "서비스 통합테스트"
        └── ApiGatewayIntegrationTest.java ← "HTTP 통합테스트"
```

**말할 내용:**
> "API Gateway 패턴을 적용한 RESTful 게시판입니다. 계층별로 깔끔하게 분리되어 있고, 모든 기능이 통합 테스트로 검증되어 있습니다."

---

### **2단계: 통합 테스트 실행 (3분)**

#### **IntelliJ에서 실행**
1. **`src/test/java` 우클릭** → `Run All Tests`
2. 또는 터미널에서: `./gradlew test --tests="*IntegrationTest"`

#### **실행 화면 설명**
```
✅ PostControllerIntegrationTest
  ├── shouldHandleFullPostLifecycle ✅
  ├── shouldFailUpdateWithWrongPassword ✅  
  └── shouldRetrieveAllPostsOrderedByDate ✅

✅ ApiGatewayIntegrationTest  
  ├── shouldReturnHealthCheck ✅
  ├── shouldCreatePostViaAPI ✅
  ├── shouldGetPostByIdViaAPI ✅
  ├── shouldUpdatePostViaAPI ✅
  ├── shouldDeletePostViaAPI ✅
  └── shouldHandleFullCRUDScenarioViaAPI ✅

🎉 BUILD SUCCESSFUL - 12 tests passed
```

**말할 내용:**
> "모든 통합 테스트가 성공했습니다! 실제 DB와 연동해서 전체 CRUD 플로우를 검증하고 있어요."

---

### **3단계: 애플리케이션 실행 (2분)**

#### **IntelliJ에서 실행**
1. **`StudyLearnSpringPostsApplication.java`** 열기
2. **▶️ Run 버튼** 클릭 또는 `Ctrl+Shift+F10`

#### **실행 로그 확인**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

:: Spring Boot ::                (v3.5.0)

Tomcat started on port 8080 (http)
H2 console available at '/h2-console'
```

**말할 내용:**
> "Spring Boot 애플리케이션이 8080 포트에서 실행됩니다. H2 인메모리 DB도 같이 동작해요!"

---

### **4단계: API 엔드포인트 테스트 (5분)**

#### **브라우저에서 테스트**

**4-1. 헬스체크 (브라우저)**
```
🌐 브라우저 주소창: http://localhost:8080/api/v1/health
📋 응답: "API Gateway is running"
```

**4-2. H2 Console 접속 (브라우저)**
```
🌐 브라우저 주소창: http://localhost:8080/h2-console
📋 설정값:
  - JDBC URL: jdbc:h2:mem:db
  - User Name: sa
  - Password: (비워두기)
```

#### **Postman/Thunder Client에서 CRUD 테스트**

**4-3. 게시글 작성 (POST)**
```http
POST http://localhost:8080/api/v1/posts
Content-Type: application/json

{
  "title": "시연용 게시글",
  "author": "발표자",
  "content": "통합테스트 시연 중입니다!",
  "password": "demo123"
}
```

**4-4. 전체 게시글 조회 (GET)**
```http
GET http://localhost:8080/api/v1/posts
```

**4-5. 특정 게시글 조회 (GET)**
```http
GET http://localhost:8080/api/v1/posts/1
```

**4-6. 게시글 수정 (PUT)**
```http
PUT http://localhost:8080/api/v1/posts/1
Content-Type: application/json

{
  "title": "수정된 시연 게시글",
  "author": "발표자",
  "content": "수정 기능도 완벽하게 동작합니다!",
  "password": "demo123"
}
```

**4-7. 잘못된 비밀번호로 수정 시도**
```http
PUT http://localhost:8080/api/v1/posts/1
Content-Type: application/json

{
  "title": "해킹 시도",
  "author": "해커",
  "content": "해킹 내용",
  "password": "wrongpassword"
}

📋 예상 응답: 400 Bad Request
{
  "timestamp": "2025-06-28T...",
  "status": 400,
  "error": "Bad Request",
  "message": "비밀번호가 일치하지 않습니다"
}
```

**4-8. 게시글 삭제 (DELETE)**
```http
DELETE http://localhost:8080/api/v1/posts/1
Content-Type: application/json

{
  "password": "demo123"
}

📋 응답: "게시글이 성공적으로 삭제되었습니다."
```

---

### **5단계: 데이터베이스 확인 (2분)**

#### **H2 Console에서 확인**
```sql
-- 테이블 구조 확인
SELECT * FROM POST;

-- 데이터 삽입 확인 (API로 생성한 후)
SELECT ID, TITLE, AUTHOR, WRITE_DATE FROM POST ORDER BY WRITE_DATE DESC;

-- 삭제 확인 (DELETE API 호출 후)
SELECT COUNT(*) FROM POST;
```

**말할 내용:**
> "실제 데이터베이스에 정확히 저장되고, 수정되고, 삭제되는 것을 확인할 수 있습니다!"

---

### **6단계: 코드 하이라이트 (1분)**

#### **IntelliJ에서 보여줄 핵심 코드**

**GlobalExceptionHandler.java**
```java
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
    // 400 Bad Request 처리
}
```

**ApiGatewayController.java**
```java
@DeleteMapping("/posts/{id}")
public ResponseEntity<String> deletePostById(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto) {
    postController.deletePost(id, postRequestDto);
    return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
}
```

---

## 💡 **시연 중 강조 포인트**

### **1. 통합 테스트의 가치**
> "Unit Test가 아닌 Integration Test로 실제 동작을 검증합니다!"

### **2. RESTful API 설계**
> "HTTP 메서드를 적절히 활용한 REST API입니다!"

### **3. 예외 처리**
> "잘못된 요청에 대해 명확한 에러 메시지를 반환합니다!"

### **4. 아키텍처 패턴**
> "API Gateway 패턴으로 확장 가능하게 설계했습니다!"

---

## 🎬 **시연 스크립트**

### **오프닝 (30초)**
> "안녕하세요! Spring Boot로 구현한 RESTful 게시판 API를 소개하겠습니다. 가장 특별한 점은 모든 기능이 통합 테스트로 검증되어 있다는 것입니다!"

### **테스트 실행 (1분)**
> "먼저 통합 테스트를 실행해보겠습니다. [테스트 실행] 보시는 것처럼 12개 테스트가 모두 성공했습니다!"

### **라이브 API 테스트 (3분)**
> "이제 실제로 API가 동작하는지 확인해보겠습니다. [애플리케이션 실행] [API 호출] 실시간으로 게시글이 생성되고, 수정되고, 삭제되는 것을 보실 수 있습니다!"

### **마무리 (30초)**
> "TDD 방법론을 적용해서 안정적이고 신뢰할 수 있는 API를 구현했습니다. 감사합니다!"

---

## 📞 **시연 중 예상 질문 & 답변**

**Q: "Mock을 안 쓰고 실제 DB를 쓰는 이유는?"**
**A:** "통합 테스트의 목적은 전체 시스템이 함께 잘 동작하는지 확인하는 것입니다. @Transactional로 테스트 격리는 보장됩니다."

**Q: "API Gateway 패턴의 장점은?"**
**A:** "단일 진입점으로 라우팅, 인증, 로깅을 처리할 수 있고, 마이크로서비스로 확장하기 쉽습니다."

**Q: "RESTful하다고 볼 수 있나요?"**
**A:** "HTTP 메서드를 의미에 맞게 사용하고, 명사형 리소스 경로를 사용했으며, 적절한 상태코드를 반환합니다."

---

## 🎯 **시연 성공 팁**

1. **미리 연습하기**: 시연 전에 2-3번 연습
2. **브라우저 탭 준비**: 필요한 URL들 북마크
3. **Postman Collection**: API 요청들 미리 저장
4. **인터넷 연결**: 안정적인 네트워크 환경
5. **백업 계획**: 스크린샷이나 녹화본 준비

---

**🎬 이제 완벽한 시연 준비 완료! 화이팅! 🚀** 