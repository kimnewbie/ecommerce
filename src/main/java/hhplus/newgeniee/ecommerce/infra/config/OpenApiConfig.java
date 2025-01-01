package hhplus.newgeniee.ecommerce.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// http://localhost:8080/swagger-ui.html
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("e-commerce API")
                        .version("1.0.0")
                        .description("HangHae-plus-backend e-commerce API documentation"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Server")
                ))
//                .components(new Components()
//                        .addSecuritySchemes("bearerAuth",
//                                new SecurityScheme()
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("bearer")
//                                        .bearerFormat("JWT")))
//                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
                ;
    }
}
