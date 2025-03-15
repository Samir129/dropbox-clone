package org.example.dropboxbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.example.dropboxbackend.repository")
public class DropboxBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DropboxBackendApplication.class, args);
    }

}
