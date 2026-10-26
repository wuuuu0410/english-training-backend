package com.wordtrainer.english.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSessionCreateCommand {
  private Integer totalCount;
  private Integer correctCount; // 可允許前端先塞 0
  private LocalDateTime endedAt; // 可選
}
