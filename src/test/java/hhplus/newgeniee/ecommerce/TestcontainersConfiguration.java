package hhplus.newgeniee.ecommerce;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * MySQL Container Test
 */
@Configuration
class TestcontainersConfiguration {

    public static final MySQLContainer<?> MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.4.3"))
                .withDatabaseName("hhplus")
                .withUsername("newgeniee")
                .withPassword("aa")
                .withInitScript("db/init.sql");
        MYSQL_CONTAINER.start();

        System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
        System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
    }

    @PreDestroy
    public void preDestroy() {
        if (MYSQL_CONTAINER.isRunning()) {
            MYSQL_CONTAINER.stop();
        }
    }
}