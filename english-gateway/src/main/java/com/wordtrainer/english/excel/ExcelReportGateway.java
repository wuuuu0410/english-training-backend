package com.wordtrainer.english.excel;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Generic Excel parsing gateway.
 *
 * @param <T> The type of the bean that corresponds to the Excel data.
 */
public interface ExcelReportGateway {

  /**
   * Parses the given Excel file into a list of beans.
   *
   * @param file  the uploaded Excel file
   * @param clazz the class type of the bean corresponding to the Excel data
   * @param <T>   the bean type
   * @return a list of parsed beans from the Excel file
   */
  <T> List<T> parse(MultipartFile file, Class<T> clazz);
}
