package com.wordtrainer.english.service.impl;

import com.wordtrainer.english.dto.QuizSessionCreateCommand;
import com.wordtrainer.english.dto.QuizSessionUpdateCommand;
import com.wordtrainer.english.entity.QuizSession;
import com.wordtrainer.english.repository.QuizSessionRepo;
import com.wordtrainer.english.service.QuizSessionSvc;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuizSessionSvcImpl implements QuizSessionSvc {

  private final QuizSessionRepo quizSessionRepo;

  @Override
  @Transactional
  public QuizSession createOne(QuizSessionCreateCommand cmd) {
    Objects.requireNonNull(cmd, "cmd must not be null");
    if (cmd.getTotalCount() == null) throw new IllegalArgumentException("totalCount is required");

    QuizSession s = new QuizSession();
    s.setTotalCount(cmd.getTotalCount());
    s.setCorrectCount(cmd.getCorrectCount() == null ? 0 : cmd.getCorrectCount());
    s.setEndedAt(cmd.getEndedAt());

    return quizSessionRepo.save(s);
  }

  @Override
  @Transactional
  public List<QuizSession> createBatch(List<QuizSessionCreateCommand> cmds) {
    if (cmds == null || cmds.isEmpty()) return List.of();
    return cmds.stream().map(this::createOne).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<QuizSession> findById(Long id) {
    if (id == null) return Optional.empty();
    return quizSessionRepo.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<QuizSession> findAll() {
    return quizSessionRepo.findAll();
  }

  @Override
  @Transactional
  public QuizSession update(Long id, QuizSessionUpdateCommand cmd) {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(cmd, "cmd must not be null");

    QuizSession s = quizSessionRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("QuizSession not found: " + id));

    if (cmd.getTotalCount() != null) s.setTotalCount(cmd.getTotalCount());
    if (cmd.getCorrectCount() != null) s.setCorrectCount(cmd.getCorrectCount());
    if (cmd.getEndedAt() != null) s.setEndedAt(cmd.getEndedAt());

    return quizSessionRepo.save(s);
  }

  @Override
  @Transactional
  public void deleteOne(Long id) {
    if (id == null) return;
    quizSessionRepo.deleteById(id);
  }

  @Override
  @Transactional
  public void deleteBatch(List<Long> ids) {
    if (ids == null || ids.isEmpty()) return;
    quizSessionRepo.deleteAllById(ids);
  }
}
