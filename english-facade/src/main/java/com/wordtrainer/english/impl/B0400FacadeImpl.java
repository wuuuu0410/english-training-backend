package com.wordtrainer.english.impl;

import com.wordtrainer.english.B0400Facade;
import com.wordtrainer.english.dto.QuestionCreateCommand;
import com.wordtrainer.english.dto.QuestionCreateReq;
import com.wordtrainer.english.dto.QuestionResp;
import com.wordtrainer.english.dto.QuestionRespCommand;
import com.wordtrainer.english.service.impl.QuestionReadingService;
import com.wordtrainer.english.utils.bean.BeanUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class B0400FacadeImpl implements B0400Facade {

  private final QuestionReadingService questionReadingService;

  @Override
  public QuestionResp create(QuestionCreateReq req) {
    QuestionCreateCommand command = BeanUtils.convert(req, QuestionCreateCommand.class);
    QuestionRespCommand created = questionReadingService.create(command);
    return BeanUtils.convert(created,QuestionResp.class);
  }

  @Override
  public List<QuestionResp> list() {
    List<QuestionRespCommand> cmds = questionReadingService.list();
    return BeanUtils.convertList(cmds,QuestionResp.class);
  }
}
