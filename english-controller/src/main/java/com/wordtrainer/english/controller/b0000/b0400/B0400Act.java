package com.wordtrainer.english.controller.b0000.b0400;

import com.wordtrainer.english.B0400Facade;
import com.wordtrainer.english.dto.QuestionCreateReq;
import com.wordtrainer.english.dto.QuestionResp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/b0400")
public class B0400Act {

  private final B0400Facade b0400Facade;

  @PostMapping("/questions")
  public QuestionResp create(@RequestBody QuestionCreateReq req) {
    return b0400Facade.create(req);
  }

  @GetMapping("/questions")
  public List<QuestionResp> list() {
    return b0400Facade.list();
  }
}

