package com.wordtrainer.english;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.wordtrainer.english.entity")
@EnableJpaRepositories("com.wordtrainer.english.repository")
public class EnglishApplication {

  public static void main(String[] args) {
    SpringApplication.run(EnglishApplication.class, args);
  }
}
