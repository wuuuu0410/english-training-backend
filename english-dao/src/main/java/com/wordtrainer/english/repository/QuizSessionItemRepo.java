package com.wordtrainer.english.repository;

import com.wordtrainer.english.entity.QuizSessionItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSessionItemRepo extends JpaRepository<QuizSessionItem, Long> {

  List<QuizSessionItem> findByQuizSession_SessionId(Long sessionId);
}
