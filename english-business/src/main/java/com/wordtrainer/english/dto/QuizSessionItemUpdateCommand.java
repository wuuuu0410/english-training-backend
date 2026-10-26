package com.wordtrainer.english.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSessionItemUpdateCommand {
  private Boolean isCorrect;
  private String userAnswer;
}
