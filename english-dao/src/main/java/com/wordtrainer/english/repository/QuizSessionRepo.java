package com.wordtrainer.english.repository;

import com.wordtrainer.english.entity.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSessionRepo extends JpaRepository<QuizSession, Long> {

}
