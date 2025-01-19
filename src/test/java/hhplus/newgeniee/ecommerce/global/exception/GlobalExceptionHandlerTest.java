package hhplus.newgeniee.ecommerce.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest  // Spring Boot 컨텍스트 로딩
@AutoConfigureMockMvc // MockMvc를 자동으로 설정
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;  // MockMvc 자동 주입

    @BeforeEach
    void setUp() { }

    @DisplayName("entityNotFoundException 예외 처리 시, 404 상태 코드와 적절한 메시지가 반환되어야 한다.")
    @Test
    void testEntityNotFoundException() throws Exception {
        // when: 예외 발생 시
        mockMvc.perform(MockMvcRequestBuilders.get("/test-entity-not-found")
                        .contentType(MediaType.APPLICATION_JSON))
                // then: 응답 검증
                .andExpect(status().isNotFound()) // 404 상태 코드
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Entity not found: [Error Message]"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(nullValue()));
    }

    @DisplayName("기타 exception 발생 시, 500 상태 코드와 적절한 메시지가 반환되어야 한다.")
    @Test
    void testHandleException() throws Exception {
        // given: 테스트 준비
        // 예외를 강제로 발생시킬 엔드포인트를 호출한다.

        // when: 예외 발생 시
        mockMvc.perform(MockMvcRequestBuilders.get("/test-internal-server-error")
                        .contentType(MediaType.APPLICATION_JSON))
                // then: 응답 검증
                .andExpect(status().isInternalServerError()) // 500 상태 코드
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Internal server error: [Error Message]"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(nullValue()));
    }

    @DisplayName("성공적인 응답은 200 상태 코드와 함께 데이터가 반환되어야 한다.")
    @Test
    void testHandleSuccess() throws Exception {
        // given: 성공적인 데이터
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1);
        data.put("name", "Test Product");

        // when: 성공적인 응답 시
        mockMvc.perform(MockMvcRequestBuilders.get("/test-success")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                // then: 응답 검증
                .andExpect(status().isOk()) // 200 상태 코드
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("Test Product"));
    }
}
