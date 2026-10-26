package com.wordtrainer.english.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "QUESTION_READING")
public class QuestionReading {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", length = 100)
  private String title;

  @Column(name = "prompt_text", columnDefinition = "TEXT", nullable = false)
  private String promptText;

  @Column(name = "opt_a", length = 500, nullable = false)
  private String optA;

  @Column(name = "opt_b", length = 500, nullable = false)
  private String optB;

  @Column(name = "opt_c", length = 500, nullable = false)
  private String optC;

  @Column(name = "opt_d", length = 500, nullable = false)
  private String optD;

  @Column(name = "answer_key", length = 1)
  private String answerKey; // A/B/C/D or null

  @Column(name = "answer_text", columnDefinition = "TEXT")
  private String answerText;

  @Column(name = "tags_json", length = 2000)
  private String tagsJson;

  @Column(name = "note_text", columnDefinition = "TEXT")
  private String noteText;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  @PrePersist
  void prePersist() {
    var now = OffsetDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = OffsetDateTime.now();
  }
}
