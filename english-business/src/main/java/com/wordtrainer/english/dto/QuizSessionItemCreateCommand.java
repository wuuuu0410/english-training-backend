package com.wordtrainer.english.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSessionItemCreateCommand {
  private Long sessionId;
  private Long wordId;
  private Boolean isCorrect;
  private String userAnswer;
}
