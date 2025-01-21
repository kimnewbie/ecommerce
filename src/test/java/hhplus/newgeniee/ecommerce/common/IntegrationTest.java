package hhplus.newgeniee.ecommerce.common;

import hhplus.newgeniee.ecommerce.util.DataCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * 통합테스트를 위한 클래스
 * Spring boot + Testcontainers를 통해
 * MySQL db를 컨테이너화된 환경에서 실행
 */
@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
public abstract class IntegrationTest {

    private static final MySQLContainer<?> mysqlContainer;

    static {
        mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.4.3"))
                .withDatabaseName("hhplus")
                .withUsername("newgeniee")
                .withPassword("aa")
                .withEnv("TZ", "UTC")
                .withInitScript("db/init.sql")
                .withReuse(true);

        mysqlContainer.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    private DataCleaner dataCleaner;

    @BeforeEach
    void setUp() {
        dataCleaner.clean();
    }
}
