package com.wordtrainer.english.dto;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResp {

  private Long id;
  private String title;
  private String promptText;
  private List<OptionDto> options;
  private String answerKey;
  private String answerText;
  private List<String> tags;
  private String noteText;
  private OffsetDateTime createdAt;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OptionDto {
    private String key;
    private String text;
  }
}
