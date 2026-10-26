package com.wordtrainer.english.dto;

import java.time.LocalDateTime;

/**
 * 錯題本統計投影（Projection）
 * 給 Repo/Dao/Service 使用，不暴露到 REST 層。
 */
public interface WordWrongView {
  Long getWordId();
  String getEn();
  String getPos();
  String getZh();
  Long getCorrectCount();
  Long getWrongCount();
  LocalDateTime getLastAskedAt();
}
