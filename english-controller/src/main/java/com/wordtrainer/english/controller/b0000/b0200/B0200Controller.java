package com.wordtrainer.english.controller.b0000.b0200;

import com.wordtrainer.english.QuizSessionFacade;
import com.wordtrainer.english.WordBankFacade;
import com.wordtrainer.english.dto.QuizSessionReq;
import com.wordtrainer.english.controller.dto.RandomWordResponse;
import com.wordtrainer.english.dto.WordPickPoolView;
import com.wordtrainer.english.dto.WordWrongViewRes;
import com.wordtrainer.english.entity.QuizSession;
import com.wordtrainer.english.entity.QuizSessionItem;
import com.wordtrainer.english.utils.bean.BeanUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("b0200/quiz")
@Log4j2
@RequiredArgsConstructor
public class B0200Controller {

  private final QuizSessionFacade quizSessionFacade;

  private final WordBankFacade wordBankFacade;

  @PostMapping("session/submit")
  public ResponseEntity<QuizSession> submit(@RequestBody QuizSessionReq req) {
    QuizSession session = quizSessionFacade.submit(req);
    return ResponseEntity.ok(session);
  }

  /**
   * 錯題本：列出所有「曾答錯過」的單字統計（答對/答錯/最後作答時間）
   */
  @GetMapping("words/wrong")
  public ResponseEntity<List<WordWrongViewRes>> findWrongWords() {
    return ResponseEntity.ok(quizSessionFacade.findWrongWords());
  }

  @GetMapping("session/{sessionId}")
  public ResponseEntity<QuizSession> findSession(@PathVariable Long sessionId) {
    return quizSessionFacade.findSession(sessionId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("session/{sessionId}/items")
  public ResponseEntity<List<QuizSessionItem>> findSessionItems(@PathVariable Long sessionId) {
    return ResponseEntity.ok(quizSessionFacade.findSessionItems(sessionId));
  }

  @GetMapping("/pickPool")
  public List<RandomWordResponse> getPickPool() {
    List<WordPickPoolView> wordPickPoolViewList = wordBankFacade.getPickPool();
    return BeanUtils.convertList(wordPickPoolViewList, RandomWordResponse.class);
  }
}
