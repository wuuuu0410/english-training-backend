package com.wordtrainer.english.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordtrainer.english.dto.DictLookupResp;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GeminiDictLookupService {

  private final WebClient geminiWebClient;
  private final ObjectMapper om;

  @Value("${gemini.model}")
  private String model;

  public DictLookupResp lookup(String term) {
    if (term == null || term.isBlank()) {
      return DictLookupResp.empty("");
    }

    String instruction = """
        你是一個英文字典 API，請查詢單字並只回傳 JSON，不要解釋。
        
        規則：
        - pos：回傳 "n." / "v." / "adj." / "adv." / "prep." ...（英文縮寫）
        - zh：繁體中文，給最常見的一個翻譯（不要塞一大串）
        - examples：至少 1 句，中英文都要；若不確定就回空陣列
        
        
        輸出 JSON 格式：
        {
          "term": "%s",
          "ipa": "",
          "pos": "",
          "zh": "",
          "examples": [
            { "en": "", "zh": "" }
          ],
          "source": "ai"
        }
        
        """.formatted(term);

    Map<String, Object> body = Map.of(
        "contents", List.of(
            Map.of(
                "role", "user",
                "parts", List.of(
                    Map.of("text", instruction)
                )
            )
        )
    );

    String responseJson = geminiWebClient.post()
        .uri("/models/{model}:generateContent", model)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class)
        .block();

    try {
      JsonNode root = om.readTree(responseJson);
      String text = root
          .path("candidates").path(0)
          .path("content").path("parts").path(0)
          .path("text").asText("");

      // Gemini 有時會包 ```json
      text = stripJsonCodeFence(text);

      return om.readValue(text, DictLookupResp.class);
    } catch (Exception e) {
      return DictLookupResp.empty(term);
    }
  }

  private static String stripJsonCodeFence(String s) {
    if (s == null) {
      return "";
    }
    String t = s.trim();
    if (t.startsWith("```")) {
      int firstNl = t.indexOf('\n');
      int last = t.lastIndexOf("```");
      if (firstNl > 0 && last > firstNl) {
        return t.substring(firstNl + 1, last).trim();
      }
    }
    return t;
  }
}
