package com.wordtrainer.english.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "QUIZ_SESSION")
public class QuizSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "SESSION_ID")
  private Long sessionId;

  @Column(name = "TOTAL_COUNT", nullable = false)
  private Integer totalCount;

  @Column(name = "CORRECT_COUNT", nullable = false)
  private Integer correctCount;

  @Column(name = "STARTED_AT", updatable = false)
  private LocalDateTime startedAt;

  @Column(name = "ENDED_AT")
  private LocalDateTime endedAt;

  @PrePersist
  protected void onCreate() {
    this.startedAt = LocalDateTime.now();
  }
}
