package hhplus.newgeniee.ecommerce;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * MySQL Container Test
 */
@Profile("testcontainers")
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    MySQLContainer<?> mysqlContainer() {
        MySQLContainer<?> container = new MySQLContainer<>(DockerImageName.parse("mysql:8.4.3"))
                .withDatabaseName("ecommerce")
                .withUsername("newgeniee")
                .withPassword("aa")
                .withEnv("TZ", "UTC")
                .withReuse(true);

        container.start();
        return container;
    }
}
