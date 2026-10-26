package com.wordtrainer.english.controller.b0000.b0100;

import com.wordtrainer.english.WordBankFacade;
import com.wordtrainer.english.dto.WordCreateCommand;
import com.wordtrainer.english.controller.dto.WordCreateRequest;
import com.wordtrainer.english.controller.dto.WordResponse;
import com.wordtrainer.english.dto.WordUpdateCommand;
import com.wordtrainer.english.controller.dto.WordUpdateRequest;
import com.wordtrainer.english.entity.WordBank;
import com.wordtrainer.english.utils.bean.BeanUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("b0100/words")
@RequiredArgsConstructor
@Log4j2
public class B0100Controller {

  private final WordBankFacade wordBankFacade;

  @GetMapping
  public List<WordResponse> list() {
    List<WordBank> wordBankList = wordBankFacade.findAll();
    return BeanUtils.convertList(wordBankList, WordResponse.class);
  }

  @PostMapping
  public WordResponse create(@RequestBody WordCreateRequest req) {
    WordBank wordBank = wordBankFacade.createOne(BeanUtils.convert(req, WordCreateCommand.class));
    return BeanUtils.convert(wordBank, WordResponse.class);
  }

  @PutMapping("/{id}")
  public WordResponse updateWord(@PathVariable("id") Long id, @RequestBody WordUpdateRequest req) {
    WordBank wordBank = wordBankFacade.update(id, BeanUtils.convert(req, WordUpdateCommand.class));
    return BeanUtils.convert(wordBank, WordResponse.class);
  }

  @DeleteMapping("/{id}")
  public void deleteOne(@PathVariable("id") Long id) {
    wordBankFacade.deleteOne(id);
  }

  @PostMapping(value = "/importExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public List<WordResponse> importExcel(@RequestPart("file") MultipartFile file) {
    List<WordBank> wordBankList = wordBankFacade.importExcel(file);
    return BeanUtils.convertList(wordBankList, WordResponse.class);
  }
}
