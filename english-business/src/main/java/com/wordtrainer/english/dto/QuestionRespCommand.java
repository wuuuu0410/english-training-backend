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
public class QuestionRespCommand {

  private Long id;
  private String title;
  private String promptText;
  private List<QuestionCreateCommand.OptionDto> options;
  private String answerKey;
  private String answerText;
  private List<String> tags;
  private String noteText;
  private OffsetDateTime createdAt;
}
