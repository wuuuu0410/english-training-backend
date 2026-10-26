package com.wordtrainer.english.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor   // 必須有無參數 constructor
@AllArgsConstructor
public class WordCreateCommand {

  private String englishWord;
  private String partOfSpeech;
  private String chineseMeaning;
  private String note;
}
