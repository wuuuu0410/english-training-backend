package com.wordtrainer.english.service;

import com.wordtrainer.english.dto.QuizSessionItemCreateCommand;
import com.wordtrainer.english.dto.QuizSessionItemUpdateCommand;
import com.wordtrainer.english.entity.QuizSessionItem;
import java.util.List;
import java.util.Optional;

public interface QuizSessionItemSvc {

  QuizSessionItem createOne(QuizSessionItemCreateCommand cmd);

  List<QuizSessionItem> createBatch(List<QuizSessionItemCreateCommand> cmds);

  Optional<QuizSessionItem> findById(Long id);

  List<QuizSessionItem> findAll();

  List<QuizSessionItem> findBySessionId(Long sessionId);

  QuizSessionItem update(Long id, QuizSessionItemUpdateCommand cmd);

  void deleteOne(Long id);

  void deleteBatch(List<Long> ids);
}
