package com.wordtrainer.english.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WordWrongViewRes {
  private Long wordId;
  private String en;
  private String pos;
  private String zh;
  private Long correctCount;
  private Long wrongCount;
  private LocalDateTime lastAskedAt;

  public static WordWrongViewRes of(
      Long wordId,
      String en,
      String pos,
      String zh,
      Long correctCount,
      Long wrongCount,
      LocalDateTime lastAskedAt
  ) {
    return new WordWrongViewRes(wordId, en, pos, zh, correctCount, wrongCount, lastAskedAt);
  }
}
