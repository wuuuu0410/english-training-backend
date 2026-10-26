package com.wordtrainer.english.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "WORD_BANK")
public class WordBank {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "WORD_ID")
  private Long wordId;

  @Column(name = "ENGLISH_WORD", nullable = false, length = 100)
  private String englishWord;

  @Column(name = "PART_OF_SPEECH", length = 20)
  private String partOfSpeech;

  @Column(name = "CHINESE_MEANING", length = 255)
  private String chineseMeaning;

  @Column(name = "NOTE", length = 500)
  private String note;

  @Column(name = "EXAM_PASS_COUNT")
  private Integer examPassCount;

  @Column(name = "MEMORIZED_LEVEL")
  private Integer memorizedLevel;

  @Column(name = "CREATED_AT", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "UPDATED_AT")
  private LocalDateTime updatedAt;

  /* ===== lifecycle ===== */
  @PrePersist
  protected void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
