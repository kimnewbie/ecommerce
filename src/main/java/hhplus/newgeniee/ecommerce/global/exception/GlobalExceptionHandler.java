package hhplus.newgeniee.ecommerce.global.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역적인 예외 처리 클래스.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 로깅을 위한 Logger 객체
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * {@link EntityNotFoundException} 예외를 처리
     * @param ex 예외 객체
     * @return 에러 응답
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        // 예외 로그
        logger.error("Entity not found: {}", ex.getMessage(), ex);

        // ApiFailResponse 형식으로 실패 응답 구성
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.NOT_FOUND.value());
        response.put("message", "Entity not found: " + ex.getMessage());
        response.put("data", null);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * 기타 모든 예외를 처리하는 기본 예외 처리기
     * @param ex 예외 객체
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        // 예외 로그
        logger.error("Internal server error: {}", ex.getMessage(), ex);

        // ApiFailResponse 형식으로 실패 응답 구성
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", "Internal server error: " + ex.getMessage());
        response.put("data", null);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 성공 응답 처리 예시
     * @param data 성공 데이터
     * @return 성공 응답
     */
    public ResponseEntity<Map<String, Object>> handleSuccess(Object data) {
        // ApiSuccessResponse 형식으로 성공 응답 구성
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.OK.value());
        response.put("message", null);
        response.put("data", data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
