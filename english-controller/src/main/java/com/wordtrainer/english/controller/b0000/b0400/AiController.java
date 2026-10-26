package com.wordtrainer.english.controller.b0000.b0400;

import com.wordtrainer.english.dto.ParseQuestionReq;
import com.wordtrainer.english.dto.ParseQuestionResp;
import com.wordtrainer.english.impl.GeminiParseQuestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

  private final GeminiParseQuestionService svc;

  public AiController(GeminiParseQuestionService svc) {
    this.svc = svc;
  }

  @PostMapping("/parse-question")
  public ParseQuestionResp parseQuestion(@RequestBody ParseQuestionReq req) {
    return svc.parse(req);
  }
}

