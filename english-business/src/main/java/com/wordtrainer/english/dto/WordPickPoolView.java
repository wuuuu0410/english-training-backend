package com.wordtrainer.english.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WordPickPoolView {

  private Long wordId;
  private String en;
  private String pos;
  private String zh;
  private Integer memorizedLevel;
  private Integer examPassCount;
  private Long correctCount;
  private Long wrongCount;
  private LocalDateTime lastAskedAt;
  private Integer weight;

}
