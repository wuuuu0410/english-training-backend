package com.wordtrainer.english.dto;

import java.time.LocalDateTime;

public interface WordPickPoolRow {
  Long getWordId();
  String getEnglishWord();
  String getPartOfSpeech();
  String getChineseMeaning();
  Integer getMemorizedLevel();
  Integer getExamPassCount();

  Long getCorrectCount();
  Long getWrongCount();
  LocalDateTime getLastAskedAt();
}
