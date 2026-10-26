package com.wordtrainer.english.controller.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class GeminiConfig {

  @Bean
  public WebClient geminiWebClient(
      @Value("${gemini.base-url}") String baseUrl,
      @Value("${gemini.api-key:}") String apiKey
  ) {
    if (apiKey == null || apiKey.isBlank()) {
      // 本地開發允許不設 key 啟動，AI 相關 API 呼叫時才會失敗
      log.warn("GEMINI_API_KEY is missing. Gemini AI features will not work.");
    }

    return WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("x-goog-api-key", apiKey)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
}
