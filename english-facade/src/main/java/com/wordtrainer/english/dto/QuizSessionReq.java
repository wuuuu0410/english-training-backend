package com.wordtrainer.english.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSessionReq {

  private List<Item> items;

  @Getter
  @Setter
  public static class Item {
    private Long wordId;
    private Boolean isCorrect;
    private String userAnswer;
  }
}
