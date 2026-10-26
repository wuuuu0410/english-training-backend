package com.wordtrainer.english.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordtrainer.english.dto.QuestionCreateCommand;
import com.wordtrainer.english.dto.QuestionRespCommand;
import com.wordtrainer.english.entity.QuestionReading;
import com.wordtrainer.english.repository.QuestionReadingRepo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionReadingService {

  private final QuestionReadingRepo repo;
  private final ObjectMapper om;

  @Transactional
  public QuestionRespCommand create(QuestionCreateCommand req) {
    validate(req);

    Map<String, String> opt = toOptionMap(req.getOptions());

    QuestionReading e = new QuestionReading();
    e.setTitle(blankToNull(req.getTitle()));
    e.setPromptText(req.getPromptText().trim());
    e.setOptA(opt.getOrDefault("A", ""));
    e.setOptB(opt.getOrDefault("B", ""));
    e.setOptC(opt.getOrDefault("C", ""));
    e.setOptD(opt.getOrDefault("D", ""));
    e.setAnswerKey(blankToNull(req.getAnswerKey()));
    e.setAnswerText(blankToNull(req.getAnswerText()));
    e.setNoteText(blankToNull(req.getNoteText()));
    e.setTagsJson(writeTags(req.getTags()));

    QuestionReading saved = repo.save(e);
    return toResp(saved);
  }

  @Transactional(readOnly = true)
  public List<QuestionRespCommand> list() {
    return repo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
        .stream().map(this::toResp).toList();
  }

  private void validate(QuestionCreateCommand req) {
    if (req == null) {
      throw new IllegalArgumentException("req is null");
    }
    if (req.getPromptText() == null || req.getPromptText().trim().isEmpty()) {
      throw new IllegalArgumentException("promptText required");
    }

    Map<String, String> opt = toOptionMap(req.getOptions());
    for (String k : List.of("A", "B", "C", "D")) {
      String v = opt.get(k);
      if (v == null || v.trim().isEmpty()) {
        throw new IllegalArgumentException("option " + k + " required");
      }
    }

    if (req.getAnswerKey() != null && !req.getAnswerKey().isBlank()) {
      if (!Set.of("A", "B", "C", "D").contains(req.getAnswerKey())) {
        throw new IllegalArgumentException("answerKey must be A/B/C/D or empty");
      }
    }
  }

  private Map<String, String> toOptionMap(List<QuestionCreateCommand.OptionDto> options) {
    Map<String, String> map = new HashMap<>();
    if (options != null) {
      for (var o : options) {
        if (o == null || o.getKey() == null) {
          continue;
        }
        map.put(o.getKey(), o.getText() == null ? "" : o.getText());
      }
    }
    return map;
  }

  private String writeTags(List<String> tags) {
    try {
      List<String> safe = (tags == null) ? List.of()
          : tags.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty())
              .distinct().toList();
      return om.writeValueAsString(safe);
    } catch (Exception e) {
      return "[]";
    }
  }

  private List<String> readTags(String tagsJson) {
    try {
      if (tagsJson == null || tagsJson.isBlank()) {
        return List.of();
      }
      return om.readValue(tagsJson, new TypeReference<List<String>>() {
      });
    } catch (Exception e) {
      return List.of();
    }
  }

  private QuestionRespCommand toResp(QuestionReading e) {
    return new QuestionRespCommand(
        e.getId(),
        nullToEmpty(e.getTitle()),
        e.getPromptText(),
        List.of(
            new QuestionCreateCommand.OptionDto("A", e.getOptA()),
            new QuestionCreateCommand.OptionDto("B", e.getOptB()),
            new QuestionCreateCommand.OptionDto("C", e.getOptC()),
            new QuestionCreateCommand.OptionDto("D", e.getOptD())
        ),
        nullToEmpty(e.getAnswerKey()),
        nullToEmpty(e.getAnswerText()),
        readTags(e.getTagsJson()),
        nullToEmpty(e.getNoteText()),
        e.getCreatedAt()
    );
  }

  private static String blankToNull(String s) {
    return (s == null || s.trim().isEmpty()) ? null : s.trim();
  }

  private static String nullToEmpty(String s) {
    return (s == null) ? "" : s;
  }
}
