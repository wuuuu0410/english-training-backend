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
public class QuestionCreateReq {

  private String title;
  private String promptText;
  private List<OptionDto> options;
  private String answerKey;   // "" or A/B/C/D
  private String answerText;
  private List<String> tags;
  private String noteText;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OptionDto {
    private String key;
    private String text;
  }
}
