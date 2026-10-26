package com.wordtrainer.english.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WordUpdateCommand {

  private String englishWord;
  private String partOfSpeech;
  private String chineseMeaning;
  private String note;
  private Integer examPassCount;
  private Integer memorizedLevel;

}
