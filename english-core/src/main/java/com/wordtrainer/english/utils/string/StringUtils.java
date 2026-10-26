package com.wordtrainer.english.utils.string;

import java.lang.reflect.Field;

public class StringUtils {

  /**
   * 將任何物件裡的字串欄位做 trim
   */
  public static <T> T normalizeStringFields(T obj) {
    if (obj == null) return null;
    try {
      for (Field field : obj.getClass().getDeclaredFields()) {
        if (field.getType() == String.class) {
          field.setAccessible(true);
          String value = (String) field.get(obj);
          if (value != null) {
            field.set(obj, value.trim());
          }
        }
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    return obj;
  }

  /**
   * 驗證某些字串欄位不能為空
   */
  public static <T> void validateNotBlank(T obj, String... requiredFields) {
    if (obj == null) throw new IllegalArgumentException("Object must not be null");

    try {
      Class<?> clazz = obj.getClass();
      for (String fieldName : requiredFields) {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object value = field.get(obj);
        if (!(value instanceof String) || org.apache.commons.lang3.StringUtils.isBlank((String) value)) {
          throw new IllegalArgumentException(fieldName + " must not be blank");
        }
      }
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}

