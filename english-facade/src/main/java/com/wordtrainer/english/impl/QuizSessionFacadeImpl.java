package com.wordtrainer.english.impl;

import com.wordtrainer.english.QuizSessionFacade;
import com.wordtrainer.english.dto.QuizSessionCreateCommand;
import com.wordtrainer.english.dto.QuizSessionItemCreateCommand;
import com.wordtrainer.english.dto.QuizSessionReq;
import com.wordtrainer.english.dto.WordWrongView;
import com.wordtrainer.english.dto.WordWrongViewRes;
import com.wordtrainer.english.entity.QuizSession;
import com.wordtrainer.english.entity.QuizSessionItem;
import com.wordtrainer.english.entity.WordBank;
import com.wordtrainer.english.service.QuizSessionItemSvc;
import com.wordtrainer.english.service.QuizSessionSvc;
import com.wordtrainer.english.service.WordBankSvc;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@RequiredArgsConstructor
@Component
public class QuizSessionFacadeImpl implements QuizSessionFacade {

  private final QuizSessionSvc quizSessionSvc;
  private final QuizSessionItemSvc quizSessionItemSvc;
  private final WordBankSvc wordBankSvc;

  @Override
  @Transactional
  public QuizSession submit(QuizSessionReq cmd) {
    validateSubmitReq(cmd);

    int totalCount = cmd.getItems().size();
    int correctCount = (int) cmd.getItems().stream()
        .filter(i -> Boolean.TRUE.equals(i.getIsCorrect()))
        .count();

    QuizSession session = createSession(totalCount, correctCount);

    List<Long> wordIds = extractWordIds(cmd);
    Map<Long, WordBank> wordMap = loadWordMapOrThrow(wordIds);

    List<QuizSessionItemCreateCommand> itemCmds = buildItemCmds(session.getSessionId(), cmd);
    quizSessionItemSvc.createBatch(itemCmds);

    Map<Long, Integer> deltaByWordId = buildDeltaByWordId(cmd);
    applyExamPassDelta(deltaByWordId);

    return session;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<QuizSession> findSession(Long sessionId) {
    if (sessionId == null) return Optional.empty();
    return quizSessionSvc.findById(sessionId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<QuizSessionItem> findSessionItems(Long sessionId) {
    if (sessionId == null) return List.of();
    return quizSessionItemSvc.findBySessionId(sessionId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<WordWrongViewRes> findWrongWords() {
    List<WordWrongView> rows = wordBankSvc.findWrongWordStats();
    return rows.stream()
        .map(r -> WordWrongViewRes.of(
            r.getWordId(),
            r.getEn(),
            r.getPos(),
            r.getZh(),
            r.getCorrectCount(),
            r.getWrongCount(),
            r.getLastAskedAt()
        ))
        .toList();
  }

  private void validateSubmitReq(QuizSessionReq cmd) {
    Objects.requireNonNull(cmd, "cmd must not be null");
    if (cmd.getItems() == null || cmd.getItems().isEmpty()) {
      throw new IllegalArgumentException("items is required");
    }
    cmd.getItems().forEach(i -> {
      if (i.getWordId() == null) throw new IllegalArgumentException("wordId is required");
      if (i.getIsCorrect() == null) throw new IllegalArgumentException("isCorrect is required");
    });
  }

  private QuizSession createSession(int totalCount, int correctCount) {
    QuizSessionCreateCommand sCmd = new QuizSessionCreateCommand();
    sCmd.setTotalCount(totalCount);
    sCmd.setCorrectCount(correctCount);
    sCmd.setEndedAt(LocalDateTime.now());
    return quizSessionSvc.createOne(sCmd);
  }

  private List<Long> extractWordIds(QuizSessionReq cmd) {
    return cmd.getItems().stream()
        .map(QuizSessionReq.Item::getWordId)
        .toList();
  }

  private Map<Long, WordBank> loadWordMapOrThrow(List<Long> wordIds) {
    List<WordBank> words = wordBankSvc.findAllByIds(wordIds);

    Map<Long, WordBank> wordMap = words.stream()
        .collect(Collectors.toMap(WordBank::getWordId, Function.identity(), (a, b) -> a));

    // 用原始 wordIds 驗證存在（避免資料存到一半才炸）
    for (Long wid : wordIds) {
      if (!wordMap.containsKey(wid)) {
        throw new IllegalArgumentException("WordBank not found: " + wid);
      }
    }
    return wordMap;
  }

  private List<QuizSessionItemCreateCommand> buildItemCmds(Long sessionId, QuizSessionReq cmd) {
    return cmd.getItems().stream().map(a -> {
      QuizSessionItemCreateCommand itCmd = new QuizSessionItemCreateCommand();
      itCmd.setSessionId(sessionId);
      itCmd.setWordId(a.getWordId());
      itCmd.setIsCorrect(a.getIsCorrect());
      itCmd.setUserAnswer(a.getUserAnswer());
      return itCmd;
    }).toList();
  }

  private Map<Long, Integer> buildDeltaByWordId(QuizSessionReq cmd) {
    Map<Long, Integer> deltaByWordId = new HashMap<>();
    for (QuizSessionReq.Item a : cmd.getItems()) {
      int delta = Boolean.TRUE.equals(a.getIsCorrect()) ? 1 : -1;
      deltaByWordId.merge(a.getWordId(), delta, Integer::sum);
    }
    return deltaByWordId;
  }

  private void applyExamPassDelta(Map<Long, Integer> deltaByWordId) {
    for (var e : deltaByWordId.entrySet()) {
      wordBankSvc.addExamPassCount(e.getKey(), e.getValue());
    }
  }

}
