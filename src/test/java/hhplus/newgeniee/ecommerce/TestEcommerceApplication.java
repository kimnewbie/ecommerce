package hhplus.newgeniee.ecommerce;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

/**
 * Main 실행 테스트 코드
 */
class TestEcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.from(EcommerceApplication::main)
                .with(TestcontainersConfiguration.class)
                .withAdditionalProfiles("testcontainers")
                .run(args);
    }
}