package com.wordtrainer.english.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 資料庫常數
 */
@Slf4j
public class DBEnum {

  /**
   * 使用者狀態
   */
  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public enum UserStatus {
    ACTIVE("ACTIVE", "啟用"),
    INACTIVE("INACTIVE", "停用"),
    LOCKED("LOCKED", "鎖定");

    private String code;
    private String cdname;

    public static UserStatus fromCode(String code) {
      for (UserStatus status : UserStatus.values()) {
        if (status.code.equals(code)) {
          return status;
        }
      }
      throw new IllegalArgumentException("Invalid UserStatus code: " + code);
    }
  }
}
