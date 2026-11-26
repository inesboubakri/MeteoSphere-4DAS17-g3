package tn.esprit.signalements_vigilance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SignalementsVigilanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SignalementsVigilanceApplication.class, args);
    }

}
