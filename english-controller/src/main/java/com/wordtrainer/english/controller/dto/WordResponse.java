package com.wordtrainer.english.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WordResponse {

  private Long wordId;
  private String englishWord;
  private String partOfSpeech;
  private String chineseMeaning;
  private String note;
  private Integer examPassCount;
  private Integer memorizedLevel;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // 計算屬性：memorizedLevelLabel
  @JsonProperty("memorizedLevelLabel")
  public String getMemorizedLevelLabel() {
    if (memorizedLevel == null) return "未定義";
    return switch (memorizedLevel) {
      case 0 -> "未接觸";
      case 1 -> "有印象";
      case 2 -> "看得懂";
      case 3 -> "用得出";
      case 4 -> "熟練";
      default -> "已內化";
    };
  }

  // 計算屬性：examStatusLabel
  @JsonProperty("examStatusLabel")
  public String getExamStatusLabel() {
    if (examPassCount == null) return "未測驗";
    if (examPassCount == 0) return "尚未通過";
    if (examPassCount <= 2) return "偶爾正確";
    if (examPassCount <= 4) return "穩定";
    return "腦袋有這個字";
  }
}
