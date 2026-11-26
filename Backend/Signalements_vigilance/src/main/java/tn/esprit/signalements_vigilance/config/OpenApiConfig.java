package tn.esprit.signalements_vigilance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI signalementsOpenApi() {
        return new OpenAPI().info(new Info()
                .title("MeteoSphere - Signalements & Vigilance")
                .version("0.0.1")
                .description("Signalements citoyens (photos, commentaires, votes, badges), vigilance par zone, "
                        + "bulletins officiels et abonnements aux notifications. Base MySQL, enregistre dans Eureka."));
    }
}
