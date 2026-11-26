package tn.esprit.air_et_sante.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI airEtSanteOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Air & Santé API")
                .description("API REST de surveillance de la qualité de l'air et de conseils santé")
                .version("1.0.0"));
    }
}
