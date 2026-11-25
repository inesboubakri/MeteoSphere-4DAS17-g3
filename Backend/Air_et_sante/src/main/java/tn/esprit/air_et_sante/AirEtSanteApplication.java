package tn.esprit.air_et_sante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AirEtSanteApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirEtSanteApplication.class, args);
    }

}
