package com.wordtrainer.english.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DictLookupResp {

  private String term;
  private String ipa;
  private String pos;
  private String zh;
  private List<DictExample> examples;
  private String source;

  public static DictLookupResp empty(String term) {
    return new DictLookupResp(term, "", "", "", List.of(), "local");
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DictExample {
    private String en;
    private String zh;
  }
}
