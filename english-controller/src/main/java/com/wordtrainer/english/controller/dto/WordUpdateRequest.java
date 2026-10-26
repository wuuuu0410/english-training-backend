package com.wordtrainer.english.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WordUpdateRequest {

  private String englishWord;
  private String partOfSpeech;
  private String chineseMeaning;
  private String note;
  private Integer examPassCount;
  private Integer memorizedLevel;

}
