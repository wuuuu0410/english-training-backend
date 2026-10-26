package com.wordtrainer.english.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordtrainer.english.dto.ParseQuestionReq;
import com.wordtrainer.english.dto.ParseQuestionResp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GeminiParseQuestionService {

  private final WebClient geminiWebClient;
  private final ObjectMapper om; // 建議你用 Spring 注入的 ObjectMapper

  @Value("${gemini.model}")
  private String model;

  public ParseQuestionResp parse(ParseQuestionReq req) {
    // 1) 基本檢查
    if (req == null || req.getImageDataUrl() == null || !req.getImageDataUrl()
        .startsWith("data:image/")) {
      return ParseQuestionResp.fail("imageDataUrl 必須是 data:image/...;base64,...");
    }

    // 2) 拆 dataURL
    DataUrl du;
    try {
      du = parseDataUrl(req.getImageDataUrl());
    } catch (Exception e) {
      return ParseQuestionResp.fail("imageDataUrl 格式不正確，無法解析圖片資料");
    }

    // 3) 呼叫 Gemini（要求回 JSON）
    String rawText;
    try {
      rawText = callGeminiForJson(du.mimeType(), du.base64Data());
    } catch (Exception e) {
      return ParseQuestionResp.fail("呼叫 Gemini 失敗：" + safeMsg(e));
    }

    // 4) 解析回來的 JSON -> ParseQuestionResp
    ParseQuestionResp parsed;
    try {
      parsed = om.readValue(rawText, ParseQuestionResp.class);
    } catch (Exception e) {
      // Gemini 有時會包一層 markdown ```json ... ```
      // 或回傳非 JSON，這裡做一次清理再嘗試
      String cleaned = stripJsonCodeFence(rawText);
      try {
        parsed = om.readValue(cleaned, ParseQuestionResp.class);
      } catch (Exception e2) {
        return ParseQuestionResp.fail("Gemini 回傳不是可解析的 JSON（請縮短指令或確認回傳格式）");
      }
    }

    // 5) 正規化（保證 A-D 一定存在）
    return normalizeOptions(parsed);
  }

  /** 呼叫 Gemini：送圖 + 指令，要求只回 JSON */
  private String callGeminiForJson(String mimeType, String base64Data) {
    String instruction = """
        你是一個 TOEIC 題目擷取引擎。請從圖片擷取成 JSON（只輸出 JSON，不要額外解釋）。
        
        規則：
        - 忠實抄寫題目文字，保留標點、括號、空格、換行。
        - options 一定要有 4 個選項 A-D（缺就用空字串）。
        - 若圖片沒有明示正確答案，answerKey="" 且 answerText=""，不要猜。
        - warnings、tags 請用繁體中文。
        - confidence 0~5。
        
        輸出 JSON 結構（欄位都要有）：
        {
          "type": "mcq",
          "title": "",
          "promptText": "",
          "options": [{"key":"A","text":""},{"key":"B","text":""},{"key":"C","text":""},{"key":"D","text":""}],
          "answerKey": "",
          "answerText": "",
          "tags": [],
          "confidence": 0,
          "warnings": [],
          "raw": { "hasImageOnly": false, "detectedOptionCount": 0, "detectedAnswerInImage": false }
        }
        """.trim();

    Map<String, Object> body = new LinkedHashMap<>();

    body.put("contents", List.of(
        Map.of(
            "role", "user",
            "parts", List.of(
                Map.of("text", instruction),
                Map.of("inlineData", Map.of(          // ✅ camelCase
                    "mimeType", mimeType,               // ✅ camelCase
                    "data", base64Data
                ))
            )
        )
    ));

// ✅ 立刻降延遲的關鍵：限制輸出 + 降隨機性 + 只要 1 個候選
    body.put("generationConfig", Map.of(
        "temperature", 0,
        "topK", 1,
        "topP", 1,
        "candidateCount", 1,
        "maxOutputTokens", 900
        // 若你這個 endpoint/版本支援，也可嘗試：
        // "responseMimeType", "application/json"
    ));

    // 可選：限制輸出 token（避免太長拖慢）
    // body.put("generationConfig", Map.of("maxOutputTokens", 1200));

    String responseJson = geminiWebClient.post()
        .uri("/models/{model}:generateContent", model)
        .bodyValue(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, resp ->
            resp.bodyToMono(String.class).flatMap(bodyText ->
                Mono.error(
                    new RuntimeException("Gemini error: " + resp.statusCode() + " " + bodyText))
            )
        )
        .bodyToMono(String.class)
        .block();

    // 解析 Gemini response：candidates[0].content.parts[0].text
    try {
      JsonNode root = om.readTree(responseJson);
      JsonNode parts = root.path("candidates").path(0).path("content").path("parts");
      if (parts.isArray() && parts.size() > 0) {
        return parts.path(0).path("text").asText("");
      }
      return "";
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse Gemini response JSON: " + safeMsg(e));
    }
  }

  /** 把 options 修正成永遠 A-D 四個 */
  private ParseQuestionResp normalizeOptions(ParseQuestionResp in) {
    Map<String, String> map = new LinkedHashMap<>();
    if (in != null && in.getOptions() != null) {
      for (ParseQuestionResp.Option o : in.getOptions()) {
        if (o != null && o.getKey() != null) {
          map.put(o.getKey(), o.getText() == null ? "" : o.getText());
        }
      }
    }

    List<ParseQuestionResp.Option> fixed = List.of(
        new ParseQuestionResp.Option("A", map.getOrDefault("A", "")),
        new ParseQuestionResp.Option("B", map.getOrDefault("B", "")),
        new ParseQuestionResp.Option("C", map.getOrDefault("C", "")),
        new ParseQuestionResp.Option("D", map.getOrDefault("D", ""))
    );

    return new ParseQuestionResp(
        safe(in == null ? null : in.getType(), "mcq"),
        safe(in == null ? null : in.getTitle(), ""),
        safe(in == null ? null : in.getPromptText(), ""),
        fixed,
        safe(in == null ? null : in.getAnswerKey(), ""),
        safe(in == null ? null : in.getAnswerText(), ""),
        in != null && in.getTags() != null ? in.getTags() : List.of(),
        clamp(in != null ? in.getConfidence() : 0, 0, 5),
        in != null && in.getWarnings() != null ? in.getWarnings() : List.of(),
        in != null && in.getRaw() != null ? in.getRaw() : new ParseQuestionResp.Raw(false, 0, false)
    );
  }

  /** data:image/png;base64,AAA... -> mime + base64 */
  private static DataUrl parseDataUrl(String dataUrl) {
    int comma = dataUrl.indexOf(',');
    if (comma < 0) {
      throw new IllegalArgumentException("missing comma");
    }

    String meta = dataUrl.substring(0, comma); // data:image/png;base64
    String base64 = dataUrl.substring(comma + 1);

    if (!meta.startsWith("data:") || !meta.contains(";base64")) {
      throw new IllegalArgumentException("not base64 data url");
    }

    String mime = meta.substring("data:".length(), meta.indexOf(";base64"));
    if (mime.isBlank() || base64.isBlank()) {
      throw new IllegalArgumentException("empty mime or data");
    }

    return new DataUrl(mime, base64);
  }

  private record DataUrl(String mimeType, String base64Data) {

  }

  private static String stripJsonCodeFence(String s) {
    if (s == null) {
      return "";
    }
    String t = s.trim();
    // ```json ... ```
    if (t.startsWith("```")) {
      int firstNl = t.indexOf('\n');
      int lastFence = t.lastIndexOf("```");
      if (firstNl > 0 && lastFence > firstNl) {
        return t.substring(firstNl + 1, lastFence).trim();
      }
    }
    return t;
  }

  private static String safe(String s, String def) {
    return s == null ? def : s;
  }

  private static int clamp(int v, int lo, int hi) {
    return Math.max(lo, Math.min(hi, v));
  }

  private static String safeMsg(Throwable e) {
    String m = e.getMessage();
    return (m == null || m.isBlank()) ? e.getClass().getSimpleName() : m;
  }
}
