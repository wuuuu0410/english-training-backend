package com.wordtrainer.english.repository;

import com.wordtrainer.english.dto.WordPickPoolRow;
import com.wordtrainer.english.dto.WordWrongView;
import com.wordtrainer.english.entity.WordBank;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WordBankRepo extends JpaRepository<WordBank, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query("""
        update WordBank w
           set w.examPassCount =
             case
               when (coalesce(w.examPassCount, 0) + :delta) < 0 then 0
               else (coalesce(w.examPassCount, 0) + :delta)
             end
         where w.wordId = :wordId
      """)
  int addExamPassCount(@Param("wordId") Long wordId, @Param("delta") int delta);

  @Query("""
        select
          w.wordId as wordId,
          w.englishWord as en,
          w.partOfSpeech as pos,
          w.chineseMeaning as zh,
          sum(case when i.isCorrect = true then 1 else 0 end) as correctCount,
          sum(case when i.isCorrect = false then 1 else 0 end) as wrongCount,
          max(i.askedAt) as lastAskedAt
        from QuizSessionItem i
        join i.wordBank w
        group by w.wordId, w.englishWord, w.partOfSpeech, w.chineseMeaning
        having sum(case when i.isCorrect = false then 1 else 0 end) > 0
        order by max(i.askedAt) desc
      """)
  List<WordWrongView> findWrongWordStats();

  @Query("""
        select
          w.wordId as wordId,
          w.englishWord as englishWord,
          w.partOfSpeech as partOfSpeech,
          w.chineseMeaning as chineseMeaning,
          w.memorizedLevel as memorizedLevel,
          w.examPassCount as examPassCount,
          coalesce(sum(case when i.isCorrect = true then 1 else 0 end), 0) as correctCount,
          coalesce(sum(case when i.isCorrect = false then 1 else 0 end), 0) as wrongCount,
          max(i.askedAt) as lastAskedAt
        from WordBank w
        left join QuizSessionItem i on i.wordBank = w
        where coalesce(w.memorizedLevel, 0) >= 1
        group by
          w.wordId, w.englishWord, w.partOfSpeech, w.chineseMeaning,
          w.memorizedLevel, w.examPassCount
      """)
  List<WordPickPoolRow> findPickPool();
}
