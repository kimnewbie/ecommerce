package hhplus.newgeniee.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

/**
 * JUnit5 테스트 코드
 */
@ActiveProfiles("testcontainers")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class EcommerceApplicationTest {

    @Test
    void contextLoads() {}

}