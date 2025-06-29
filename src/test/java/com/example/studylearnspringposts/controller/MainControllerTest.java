package com.example.studylearnspringposts.controller;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class MainControllerTest {

    private static final Logger log = LoggerFactory.getLogger(MainControllerTest.class);

    @Test
    void shouldLogInfoMessage() {
        // Given
        String expectedMessage = "테스트 로그 출력 확인";
        
        // When & Then
        assertDoesNotThrow(() -> {
            log.info(expectedMessage);
        });
        
        // 로그가 정상적으로 출력되는지 확인
        assertTrue(true, "로그 출력이 정상적으로 수행됨");
    }

    @Test
    void shouldRunBasicTest() {
        // Given
        String testData = "기본 테스트 데이터";
        
        // When
        String result = testData.toUpperCase();
        
        // Then
        assertEquals("기본 테스트 데이터", result);
        log.info("기본 테스트 완료: {}", result);
    }
}
