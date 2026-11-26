package tn.esprit.prevision.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI previsionOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Prévisions Météo API")
                .description("API REST de gestion des prévisions météorologiques (MS3)")
                .version("1.0.0"));
    }
}
