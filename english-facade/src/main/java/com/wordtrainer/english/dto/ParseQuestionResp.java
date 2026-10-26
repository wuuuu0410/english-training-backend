package com.wordtrainer.english.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParseQuestionResp {

  private String type;            // mcq
  private String title;           // 可空字串
  private String promptText;      // 題幹（含空格/換行）
  private List<Option> options;   // 永遠 4 筆 A-D
  private String answerKey;       // "", "A","B","C","D"（看圖片有沒有明示）
  private String answerText;      // 可空
  private List<String> tags;      // 可空 list
  private int confidence;         // 0-5
  private List<String> warnings;  // 可空 list（用繁中）
  private Raw raw;                // 偵測資訊

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Option {
    private String key;
    private String text;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Raw {
    private boolean hasImageOnly;
    private int detectedOptionCount;
    private boolean detectedAnswerInImage;
  }

  /** 給失敗時的預設回傳，避免前端爆掉 */
  public static ParseQuestionResp fail(String warning) {
    return new ParseQuestionResp(
        "mcq",
        "",
        "",
        List.of(
            new Option("A", ""),
            new Option("B", ""),
            new Option("C", ""),
            new Option("D", "")
        ),
        "",
        "",
        List.of(),
        0,
        List.of(warning == null ? "解析失敗" : warning),
        new Raw(false, 0, false)
    );
  }
}
