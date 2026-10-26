package com.wordtrainer.english.impl;

import com.wordtrainer.english.WordBankFacade;
import com.wordtrainer.english.dto.WordCreateCommand;
import com.wordtrainer.english.dto.WordPickPoolView;
import com.wordtrainer.english.dto.WordUpdateCommand;
import com.wordtrainer.english.entity.WordBank;
import com.wordtrainer.english.excel.ExcelReportGateway;
import com.wordtrainer.english.service.WordBankSvc;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RequiredArgsConstructor
@Component
public class WordBankFacadeImpl implements WordBankFacade {

  private final WordBankSvc wordBankSvc;

  private final ExcelReportGateway excelReportGateway;

  @Override
  public WordBank createOne(WordCreateCommand req) {
    return wordBankSvc.createOne(req);
  }

  @Override
  public List<WordBank> importExcel(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return List.of();
    }

    // Four columns: English word, part of speech, Chinese meaning, note
    List<WordCreateCommand> cmds = excelReportGateway.parse(file, WordCreateCommand.class);

    return wordBankSvc.createBatch(cmds);
  }

  @Override
  public List<WordBank> findAll() {
    return wordBankSvc.findAll();
  }

  @Override
  public WordBank update(Long id, WordUpdateCommand req) {
    return wordBankSvc.update(id, req);
  }

  @Override
  public void deleteOne(Long id) {
    wordBankSvc.deleteOne(id);
  }

  @Override
  public List<WordPickPoolView> getPickPool() {
    return wordBankSvc.getPickPool();
  }
}
