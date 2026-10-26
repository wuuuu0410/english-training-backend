package com.wordtrainer.english.service;

import com.wordtrainer.english.dto.QuizSessionCreateCommand;
import com.wordtrainer.english.dto.QuizSessionUpdateCommand;
import com.wordtrainer.english.entity.QuizSession;
import java.util.List;
import java.util.Optional;

public interface QuizSessionSvc {

  QuizSession createOne(QuizSessionCreateCommand cmd);

  List<QuizSession> createBatch(List<QuizSessionCreateCommand> cmds);

  Optional<QuizSession> findById(Long id);

  List<QuizSession> findAll();

  QuizSession update(Long id, QuizSessionUpdateCommand cmd);

  void deleteOne(Long id);

  void deleteBatch(List<Long> ids);
}
