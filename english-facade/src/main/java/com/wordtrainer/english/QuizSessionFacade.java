package com.wordtrainer.english;

import com.wordtrainer.english.dto.QuizSessionReq;
import com.wordtrainer.english.dto.WordWrongViewRes;
import com.wordtrainer.english.entity.QuizSession;
import com.wordtrainer.english.entity.QuizSessionItem;
import java.util.List;
import java.util.Optional;

public interface QuizSessionFacade {

  QuizSession submit(QuizSessionReq cmd);

  Optional<QuizSession> findSession(Long sessionId);

  List<QuizSessionItem> findSessionItems(Long sessionId);

  List<WordWrongViewRes> findWrongWords();
}
