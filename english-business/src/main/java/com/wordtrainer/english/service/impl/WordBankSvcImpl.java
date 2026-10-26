package com.wordtrainer.english.service.impl;

import com.wordtrainer.english.dto.WordCreateCommand;
import com.wordtrainer.english.dto.WordPickPoolView;
import com.wordtrainer.english.dto.WordUpdateCommand;
import com.wordtrainer.english.dto.WordWrongView;
import com.wordtrainer.english.entity.WordBank;
import com.wordtrainer.english.repository.WordBankRepo;
import com.wordtrainer.english.service.WordBankSvc;
import com.wordtrainer.english.utils.bean.BeanUtils;
import com.wordtrainer.english.utils.string.StringUtils;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class WordBankSvcImpl implements WordBankSvc {

  private final WordBankRepo wordBankRepo;

  public static final String ENGLISH_WORD = "englishWord";
  public static final String PART_OF_SPEECH = "partOfSpeech";
  public static final String CHINESE_MEANING = "chineseMeaning";

  @Override
  @Transactional
  public WordBank createOne(WordCreateCommand cmd) {
    // Trim all string fields to remove leading and trailing whitespace
    StringUtils.normalizeStringFields(cmd);
    // Validate required fields
    StringUtils.validateNotBlank(cmd, ENGLISH_WORD, PART_OF_SPEECH, CHINESE_MEANING);

    WordBank bank = BeanUtils.convert(cmd, WordBank.class);
    bank.setMemorizedLevel(0);
    bank.setExamPassCount(0);

    return wordBankRepo.save(bank);
  }

  @Override
  @Transactional
  public List<WordBank> createBatch(List<WordCreateCommand> cmds) {
    List<WordBank> entities = cmds.stream()
        .map(cmd -> BeanUtils.convert(cmd, WordBank.class))
        .peek(e -> {
          e.setMemorizedLevel(0);
          e.setExamPassCount(0);
        })
        .toList();
    return wordBankRepo.saveAll(entities);
  }


  @Override
  public List<WordBank> findAll() {
    return wordBankRepo.findAll();
  }

  @Override
  @Transactional
  public WordBank update(Long id, WordUpdateCommand cmd) {
    // Trim all string fields to remove leading and trailing whitespace
    StringUtils.normalizeStringFields(cmd);

    WordBank e = wordBankRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("No data found for id=" + id));

    e.setEnglishWord(cmd.getEnglishWord());
    e.setPartOfSpeech(cmd.getPartOfSpeech());
    e.setChineseMeaning(cmd.getChineseMeaning());
    e.setNote(cmd.getNote());
    e.setMemorizedLevel(Math.max(0, Math.min(4, cmd.getMemorizedLevel())));

    return wordBankRepo.save(e);
  }


  @Override
  @Transactional
  public void deleteOne(Long id) {
    wordBankRepo.findById(id).ifPresent(wordBankRepo::delete);
  }

  @Override
  @Transactional
  public List<WordBank> findAllByIds(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }

    return wordBankRepo.findAllById(ids);
  }

  @Override
  @Transactional
  public void addExamPassCount(Long wordId, int delta) {
    Objects.requireNonNull(wordId, "wordId must not be null");
    if (delta == 0) {
      return;
    }

    int updated = wordBankRepo.addExamPassCount(wordId, delta);
    if (updated == 0) {
      throw new IllegalArgumentException("WordBank not found: " + wordId);
    }
  }

  @Override
  public List<WordWrongView> findWrongWordStats() {
    return wordBankRepo.findWrongWordStats();
  }

  @Override
  public List<WordPickPoolView> getPickPool() {
    var rows = wordBankRepo.findPickPool();

    return rows.stream().map(row -> new WordPickPoolView(
        row.getWordId(),
        row.getEnglishWord(),
        row.getPartOfSpeech(),
        row.getChineseMeaning(),
        row.getMemorizedLevel() == null ? 0 : row.getMemorizedLevel(),
        row.getExamPassCount() == null ? 0 : row.getExamPassCount(),
        row.getCorrectCount(),
        row.getWrongCount(),
        row.getLastAskedAt(),
        calculateWeight(row.getExamPassCount(), row.getMemorizedLevel(), row.getWrongCount())
    )).toList();
  }

  private int calculateWeight(Integer examPassCount, Integer memorizedLevel, Long wrongCount) {
    int pass = examPassCount == null ? 0 : examPassCount;
    long wrong = wrongCount == null ? 0 : wrongCount;

    int weight = 1;

    // examPassCount = 0, 權重更高
    if (pass == 0) {
      weight += 4;
    }

    // 錯題（曾錯過）權重更高
    if (wrong > 0) {
      weight += 5;
    }

    // 錯越多越常出（上限 5）
    weight += (int) Math.min(wrong, 5);

    return weight;
  }

}
