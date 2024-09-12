package com.product;

import com.product.config.PubSubConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
@EnableConfigurationProperties(PubSubConfiguration.class)
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/api-docs.yaml")
    public ResponseEntity<String> getApiDocs() throws IOException {
        ClassPathResource resource = new ClassPathResource("docs/api-docs.yaml");
        Path path = resource.getFile().toPath();
        String content = Files.readString(path);
        return ResponseEntity.ok()
                .header("Content-Type", "application/yaml")
                .body(content);
    }

}

