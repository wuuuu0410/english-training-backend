package com.wordtrainer.english.utils.bean;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

/**
 * Bean 轉換工具類.
 *
 * @author Ben.Chen
 * @date 2022-10-04
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanUtils {

  /**
   * 預設轉換器
   */
  private static final Converter DEFAULT_CONVERTER = new DefaultConvert();


  /**
   * 取得欄位 get method
   *
   * @param name  the name
   * @param clazz the clazz
   * @return the field get method
   * @author Ben.Chen
   * @date 2022-10-14
   */
  public static Optional<Method> getFieldGetMethod(String name, Class<?> clazz) {
    return Stream.of(clazz.getMethods())
        .filter(method -> StringUtils.equalsIgnoreCase(
            "get" + CaseUtils.toCamelCase(name, true), method.getName())).findFirst();
  }

  /**
   * 取得欄位 set method
   *
   * @param name  the name
   * @param clazz the clazz
   * @return the field get method
   * @author Ben.Chen
   * @date 2022-12-28
   */
  public static Optional<Method> getFieldSetMethod(String name, Class<?> clazz) {
    return Stream.of(clazz.getMethods())
        .filter(method -> StringUtils.equalsIgnoreCase(
            "set" + CaseUtils.toCamelCase(name, true), method.getName())).findFirst();
  }

  /**
   * 取得對象欄位值
   *
   * @param name   the name
   * @param target the target
   * @param args   the args
   * @return the field get method
   * @throws ReflectiveOperationException the reflective operation
   * @author Ben.Chen
   * @date 2022-10-14
   */
  public static Optional<Object> getFieldVal(String name, Object target, Object... args)
      throws ReflectiveOperationException {
    Optional<Method> methodOptional = getFieldGetMethod(name, target.getClass());

    if (methodOptional.isPresent() && methodOptional.get().canAccess(target)) {
      return Optional.ofNullable(methodOptional.get().invoke(target, args));
    }

    return Optional.empty();
  }

  /**
   * 物件間集合轉換
   *
   * @param <T>         轉換目標型別
   * @param src         來源物件
   * @param targetClazz 目標物件類型
   * @return 轉換後物件
   * @author Ben.Chen
   * @date 2022-10-04
   */
  public static <T> List<T> convertList(
      List<?> src, Class<T> targetClazz) {
    return BeanUtils.convertList(src, targetClazz, null);
  }


  /**
   * 物件間集合轉換
   *
   * @param <T>         轉換目標型別
   * @param src         來源物件
   * @param targetClazz 目標物件類型
   * @param converter   自訂轉換器
   * @return 轉換後物件
   * @author Ben.Chen
   * @date 2022-10-04
   */
  public static <T> List<T> convertList(
      List<?> src, Class<T> targetClazz, Converter converter) {
    return src.stream()
        .map(srcObj -> BeanUtils.convert(srcObj, targetClazz, converter))
        .collect(Collectors.toList());
  }


  /**
   * 物件間轉換
   *
   * @param <T>         轉換目標型別
   * @param src         來源物件
   * @param targetClazz 目標物件類型
   * @return 轉換後物件
   * @author Ben.Chen
   * @date 2022-10-04
   */
  public static <T> T convert(Object src, Class<T> targetClazz) {
    return BeanUtils.convert(src, targetClazz, null);
  }

  /**
   * 物件間轉換
   *
   * @param <T>         轉換目標型別
   * @param src         來源物件
   * @param targetClazz 目標物件類型
   * @param converter   自訂轉換器
   * @return 轉換後物件
   * @author Ben.Chen
   * @date 2022-10-04
   */
  public static <T> T convert(Object src, Class<T> targetClazz, Converter converter) {
    converter = Optional.ofNullable(converter).orElse(DEFAULT_CONVERTER);
    try {
      BeanCopier beanCopier = getBeanCopier(src.getClass(), targetClazz);
      T targetBean = targetClazz.getDeclaredConstructor().newInstance();
      beanCopier.copy(src, targetBean, converter);
      return targetBean;
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * 物件間轉換
   *
   * @param <T>       轉換目標型別
   * @param src       來源物件
   * @param supplier  目標物件提供器
   * @param converter 自訂轉換器
   * @return 轉換後物件 t
   * @author Ben.Chen
   * @date 2022-10-28
   */
  public static <T> T convert(Object src, Supplier<T> supplier, Converter converter) {
    converter = Optional.ofNullable(converter).orElse(DEFAULT_CONVERTER);
    T targetBean = supplier.get();
    BeanCopier beanCopier = getBeanCopier(src.getClass(), targetBean.getClass());
    beanCopier.copy(src, targetBean, converter);
    return targetBean;
  }


  /**
   * 物件間集合轉換
   *
   * @param <T>      轉換目標型別
   * @param src      來源物件
   * @param supplier 目標物件提供器
   * @return 轉換後物件 list
   * @author Ben.Chen
   * @date 2022-10-28
   */
  public static <T> List<T> convertList(
      List<?> src, Supplier<T> supplier) {
    return BeanUtils.convertList(src, supplier, null);
  }

  /**
   * 物件間轉換
   *
   * @param <S>       來源目標型別
   * @param <R>       轉換目標型別
   * @param src       來源物件
   * @param function  目標物件提供器
   * @param converter 自訂轉換器
   * @return 轉換後物件 R
   * @author Ben.Chen
   * @date 2022-10-28
   */
  public static <S, R> R convert(S src, Function<S, R> function, Converter converter) {
    converter = Optional.ofNullable(converter).orElse(DEFAULT_CONVERTER);
    R targetBean = function.apply(src);
    BeanCopier beanCopier = getBeanCopier(src.getClass(), targetBean.getClass());
    beanCopier.copy(src, targetBean, converter);
    return targetBean;
  }


  /**
   * 物件間集合轉換
   *
   * @param <T>       轉換目標型別
   * @param src       來源物件
   * @param supplier  目標物件提供器
   * @param converter 自訂轉換器
   * @return 轉換後物件 list
   * @author Ben.Chen
   * @date 2022-10-28
   */
  public static <T> List<T> convertList(
      List<?> src, Supplier<T> supplier, Converter converter) {
    return src.stream()
        .map(srcObj -> BeanUtils.convert(srcObj, supplier, converter))
        .collect(Collectors.toList());
  }

  /**
   * 物件間集合轉換
   *
   * @param <S>      來源目標型別
   * @param <R>      轉換目標型別
   * @param src      來源物件
   * @param function 目標物件提供器
   * @return 轉換後物件 list
   * @author Ben.Chen
   * @date 2022-10-28
   */
  public static <S, R> List<R> convertList(
      List<S> src, Function<S, R> function) {
    return BeanUtils.convertList(src, function, null);
  }


  /**
   * 物件間集合轉換
   *
   * @param <S>       來源目標型別
   * @param <R>       轉換目標型別
   * @param src       來源物件
   * @param function  目標物件提供器
   * @param converter 自訂轉換器
   * @return 轉換後物件 list
   * @author Ben.Chen
   * @date 2022-10-28
   */
  public static <S, R> List<R> convertList(
      List<S> src, Function<S, R> function, Converter converter) {
    return src.stream()
        .map(srcObj -> BeanUtils.convert(srcObj, function, converter))
        .collect(Collectors.toList());
  }

  /**
   * 基本型態 <--> 包裝類型 Converter
   *
   * @author Ben.Chen
   * @date 2022-10-04
   */
  @NoArgsConstructor
  private static class DefaultConvert implements Converter {

    /**
     * 直接回傳 value 觸發 autoboxing/unboxing 進行基本型態 <--> 包裝類型轉換
     * <p>
     * 欄位名稱相同但類型不同則不轉換
     *
     * @param srcVal        來源值
     * @param targetClazz   轉換類型
     * @param setMethodName set method 名稱
     * @return val
     * @author Ben.Chen
     * @date 2022-10-04
     */
    @Override
    public Object convert(Object srcVal, Class targetClazz, Object setMethodName) {
      if (Objects.nonNull(srcVal) && !srcVal.getClass().equals(targetClazz)
          && !isPrimitiveOrWrapperEquivalent(srcVal.getClass(), targetClazz)) {
        if (srcVal instanceof Timestamp timestamp) {
          srcVal = convertTimestamp(timestamp, targetClazz);
        } else if (targetClazz.equals(Timestamp.class)) {
          srcVal = convertToTimestamp(srcVal);
        } else {
          srcVal = targetClazz.cast(null);
        }
      }
      return srcVal;
    }

    /**
     * 將基本型別轉換成包裝類型後比較是否相同
     *
     * @param class1 the class 1
     * @param class2 the class 2
     * @return the boolean
     * @author Ben.Chen
     * @date 2023-05-22
     */
    private boolean isPrimitiveOrWrapperEquivalent(Class<?> class1, Class<?> class2) {
      if (class1.isPrimitive()) {
        class1 = primitiveToWrapper(class1);
      }
      if (class2.isPrimitive()) {
        class2 = primitiveToWrapper(class2);
      }
      return class1.equals(class2);
    }

    /**
     * 基本型別轉換包裝類型
     *
     * @param primitiveClass the primitive class
     * @return the class
     * @author Ben.Chen
     * @date 2023-05-22
     */
    private Class<?> primitiveToWrapper(Class<?> primitiveClass) {
      Class<?> wrapperClass = primitiveClass;

      if (primitiveClass.isPrimitive()) {
        if (primitiveClass.equals(boolean.class)) {
          wrapperClass = Boolean.class;
        } else if (primitiveClass.equals(int.class)) {
          wrapperClass = Integer.class;
        } else if (primitiveClass.equals(long.class)) {
          wrapperClass = Long.class;
        } else if (primitiveClass.equals(short.class)) {
          wrapperClass = Short.class;
        } else if (primitiveClass.equals(byte.class)) {
          wrapperClass = Byte.class;
        } else if (primitiveClass.equals(double.class)) {
          wrapperClass = Double.class;
        } else if (primitiveClass.equals(float.class)) {
          wrapperClass = Float.class;
        } else if (primitiveClass.equals(char.class)) {
          wrapperClass = Character.class;
        } else {
          return wrapperClass;
        }
      }

      return wrapperClass;
    }

    /**
     * 其他日期格式轉 timestamp
     *
     * @param time
     * @return
     */
    private Object convertToTimestamp(Object time) {
      Object result = Timestamp.class.cast(null);
      if (time instanceof LocalDate ld) {
        result = Timestamp.valueOf(ld.atStartOfDay());
      } else if (time instanceof LocalDateTime ldt) {
        result = Timestamp.valueOf(ldt);
      } else if (time instanceof Date date) {
        result = new Timestamp(date.getTime());
      } else {
        return result;
      }
      return result;
    }

    /**
     * 轉換 timeStamp to LocalDate / LocalDateTime
     *
     * @param srcVal      the src val
     * @param targetClazz the target clazz
     * @return the object
     * @author Ben.Chen
     * @date 2022-11-28
     */
    private Object convertTimestamp(Timestamp srcVal, Class<?> targetClazz) {
      Object newVal;
      if (targetClazz == LocalDateTime.class) {
        newVal = srcVal.toLocalDateTime();
      } else if (targetClazz == LocalDate.class) {
        newVal = srcVal.toLocalDateTime().toLocalDate();
      } else {
        newVal = srcVal;
      }
      return newVal;
    }
  }


  /**
   * 取 beanCopier
   *
   * @param srcClazz    the src clazz 待轉類別
   * @param targetClazz the target clazz 欲轉類別
   * @return the bean copier
   */
  public static BeanCopier getBeanCopier(Class<?> srcClazz, Class<?> targetClazz) {
    return BeanCopier.create(srcClazz, targetClazz, true);
  }

  /**
   * 壓縮zip檔案
   *
   * @param bytes
   * @return the object
   * @author Claire.Hsu
   * @date 2024-03-20
   */
  public static byte[] copyByte(byte[] bytes) {
    return bytes != null ? bytes.clone() : null;
  }
}
