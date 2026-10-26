package com.wordtrainer.english;

import com.wordtrainer.english.dto.QuestionCreateReq;
import com.wordtrainer.english.dto.QuestionResp;
import java.util.List;

public interface B0400Facade {

  QuestionResp create(QuestionCreateReq req);

  List<QuestionResp> list();
}
