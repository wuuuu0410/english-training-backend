package com.wordtrainer.english.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSessionUpdateCommand {
  private Integer totalCount;
  private Integer correctCount;
  private LocalDateTime endedAt;
}
