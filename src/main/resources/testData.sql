-- 사용자 테스트 데이터 (BCrypt 암호화된 비밀번호)
-- testuser : password123
INSERT INTO users (username, password, role, age) VALUES ('testuser', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ROLE_USER', 25);
-- admin : admin123  
INSERT INTO users (username, password, role, age) VALUES ('admin', '$2a$10$X5wFBtLrL/kHcmrOewaOdOGNd5A4mBYyAs8kcIKKhLSLlVdZWrYoO', 'ROLE_ADMIN', 30);

-- 게시글 테스트 데이터  
INSERT INTO post (title, author, password, content, write_date, created_date, updated_date) VALUES 
('첫 번째 게시글', '홍길동', 'password123', '이것은 첫 번째 게시글의 내용입니다. API 게이트웨이 패턴을 적용한 Spring Boot 애플리케이션에서 작성된 글입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO post (title, author, password, content, write_date, created_date, updated_date) VALUES 
('Spring Boot 학습기', '김개발', 'mypass456', 'Spring Boot를 이용한 API 게이트웨이 패턴 구현에 대해 공부하고 있습니다. JPA와 H2 데이터베이스를 사용하여 게시판을 만들어보았습니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO post (title, author, password, content, write_date, created_date, updated_date) VALUES 
('API 설계 가이드', '이백엔드', 'secure789', 'RESTful API 설계 시 고려해야 할 사항들과 API 게이트웨이 패턴의 장점에 대해 정리해보았습니다. 마이크로서비스 아키텍처에서는 필수적인 패턴입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO post (title, author, password, content, write_date, created_date, updated_date) VALUES 
('게시판 CRUD 완성', '박프론트', 'frontend123', '게시글 생성, 조회, 수정, 삭제 기능을 모두 구현했습니다. 이제 프론트엔드와 연동하여 완전한 웹 애플리케이션을 만들 예정입니다.', CURRENT_TIMESTAMP - 3 HOUR, CURRENT_TIMESTAMP - 3 HOUR, CURRENT_TIMESTAMP - 3 HOUR);

INSERT INTO post (title, author, password, content, write_date, created_date, updated_date) VALUES 
('테스트 주도 개발', '정테스터', 'tdd2024', 'TDD 방법론을 적용하여 견고한 코드를 작성하는 방법에 대해 학습했습니다. 단위 테스트와 통합 테스트의 중요성을 깨달았습니다.', CURRENT_TIMESTAMP - 4 HOUR, CURRENT_TIMESTAMP - 4 HOUR, CURRENT_TIMESTAMP - 4 HOUR);
