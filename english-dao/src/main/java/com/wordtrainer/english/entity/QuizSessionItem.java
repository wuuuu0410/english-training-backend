package com.wordtrainer.english.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "QUIZ_SESSION_ITEM")
public class QuizSessionItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ITEM_ID")
  private Long itemId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "SESSION_ID", nullable = false)
  private QuizSession quizSession;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "WORD_ID", nullable = false)
  private WordBank wordBank;

  @Column(name = "IS_CORRECT", nullable = false)
  private Boolean isCorrect;

  @Column(name = "USER_ANSWER", length = 255)
  private String userAnswer;

  @Column(name = "ASKED_AT", updatable = false)
  private LocalDateTime askedAt;

  @PrePersist
  protected void onCreate() {
    this.askedAt = LocalDateTime.now();
  }
}
