package com.wordtrainer.english.controller.b0000.b0400;

import com.wordtrainer.english.dto.DictLookupResp;
import com.wordtrainer.english.impl.GeminiDictLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/b0400/dict")
public class B0400DictController {

  private final GeminiDictLookupService dictSvc;

  @GetMapping("/lookup")
  public DictLookupResp lookup(@RequestParam("term") String term) {
    return dictSvc.lookup(term);
  }
}


