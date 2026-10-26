package com.wordtrainer.english.service.impl;

import com.wordtrainer.english.dto.QuizSessionItemCreateCommand;
import com.wordtrainer.english.dto.QuizSessionItemUpdateCommand;
import com.wordtrainer.english.entity.QuizSession;
import com.wordtrainer.english.entity.QuizSessionItem;
import com.wordtrainer.english.entity.WordBank;
import com.wordtrainer.english.repository.QuizSessionItemRepo;
import com.wordtrainer.english.repository.QuizSessionRepo;
import com.wordtrainer.english.repository.WordBankRepo;
import com.wordtrainer.english.service.QuizSessionItemSvc;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuizSessionItemSvcImpl implements QuizSessionItemSvc {

  private final QuizSessionItemRepo itemRepo;
  private final QuizSessionRepo sessionRepo;
  private final WordBankRepo wordBankRepo;

  @Override
  @Transactional
  public QuizSessionItem createOne(QuizSessionItemCreateCommand cmd) {
    Objects.requireNonNull(cmd, "cmd must not be null");
    if (cmd.getSessionId() == null) throw new IllegalArgumentException("sessionId is required");
    if (cmd.getWordId() == null) throw new IllegalArgumentException("wordId is required");
    if (cmd.getIsCorrect() == null) throw new IllegalArgumentException("isCorrect is required");

    QuizSession session = sessionRepo.findById(cmd.getSessionId())
        .orElseThrow(() -> new IllegalArgumentException("QuizSession not found: " + cmd.getSessionId()));

    WordBank word = wordBankRepo.findById(cmd.getWordId())
        .orElseThrow(() -> new IllegalArgumentException("WordBank not found: " + cmd.getWordId()));

    QuizSessionItem it = new QuizSessionItem();
    it.setQuizSession(session);
    it.setWordBank(word);
    it.setIsCorrect(cmd.getIsCorrect());
    it.setUserAnswer(cmd.getUserAnswer());

    return itemRepo.save(it);
  }

  @Override
  @Transactional
  public List<QuizSessionItem> createBatch(List<QuizSessionItemCreateCommand> cmds) {
    if (cmds == null || cmds.isEmpty()) return List.of();
    return cmds.stream().map(this::createOne).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<QuizSessionItem> findById(Long id) {
    if (id == null) return Optional.empty();
    return itemRepo.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<QuizSessionItem> findAll() {
    return itemRepo.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public List<QuizSessionItem> findBySessionId(Long sessionId) {
    if (sessionId == null) return List.of();
    return itemRepo.findByQuizSession_SessionId(sessionId);
  }

  @Override
  @Transactional
  public QuizSessionItem update(Long id, QuizSessionItemUpdateCommand cmd) {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(cmd, "cmd must not be null");

    QuizSessionItem it = itemRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("QuizSessionItem not found: " + id));

    if (cmd.getIsCorrect() != null) it.setIsCorrect(cmd.getIsCorrect());
    if (cmd.getUserAnswer() != null) it.setUserAnswer(cmd.getUserAnswer());

    return itemRepo.save(it);
  }

  @Override
  @Transactional
  public void deleteOne(Long id) {
    if (id == null) return;
    itemRepo.deleteById(id);
  }

  @Override
  @Transactional
  public void deleteBatch(List<Long> ids) {
    if (ids == null || ids.isEmpty()) return;
    itemRepo.deleteAllById(ids);
  }
}
