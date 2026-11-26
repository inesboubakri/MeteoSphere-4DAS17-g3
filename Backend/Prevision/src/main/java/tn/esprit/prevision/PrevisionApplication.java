package tn.esprit.prevision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class PrevisionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrevisionApplication.class, args);
    }

}
