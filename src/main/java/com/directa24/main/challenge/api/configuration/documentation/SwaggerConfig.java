package com.directa24.main.challenge.api.configuration.documentation;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Directa24 API")
                        .version("v1.0")
                        .description("API documentation for the Directa24 back-end dev challenge.")
                        .contact(new Contact()
                                .name("Juan Lagoria")
                                .email("lagoria.work@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local environment")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Directa24 Documentation")
                        .url("https://docs.directa24.com"));
    }

    @Bean
    public GroupedOpenApi directorsApi() {
        return GroupedOpenApi.builder()
                .group("directors")
                .pathsToMatch("/api/v1/movies/directors/**")
                .build();
    }

}
