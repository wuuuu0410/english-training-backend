package com.wordtrainer.english.excel.impl;

import com.wordtrainer.english.excel.ExcelReportGateway;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Component
public class ExcelReportGatewayImpl implements ExcelReportGateway {

  @Override
  public <T> List<T> parse(MultipartFile file, Class<T> clazz) {
    if (file == null || file.isEmpty()) {
      return List.of();
    }

    try (InputStream is = file.getInputStream();
        Workbook wb = new XSSFWorkbook(is)) {

      Sheet sheet = wb.getSheetAt(0);
      List<T> result = new ArrayList<>();

      for (int r = 1; r <= sheet.getLastRowNum(); r++) {
        Row row = sheet.getRow(r);
        if (row == null) {
          continue;
        }

        T t = clazz.getDeclaredConstructor().newInstance();

        for (int c = 0; c < row.getLastCellNum(); c++) {
          Cell cell = row.getCell(c);
          if (cell == null) {
            continue;
          }

          // 假設 Bean 有 setter，按列順序對應
          Method setter = clazz.getMethods()[c]; // 簡單示例，實務可用 Map<colIndex, setter>
          Class<?> paramType = setter.getParameterTypes()[0];
          Object value = convertCell(cell, paramType);
          setter.invoke(t, value);
        }

        // Check if the entire row is empty
        boolean isEmpty = Stream.of(t.getClass().getMethods())
            .anyMatch(m -> m.getName().startsWith("get")); // Simple check, can be improved
        if (!isEmpty) {
          result.add(t);
        }
      }

      return result;

    } catch (Exception e) {
      log.error("Excel解析失敗", e);
      throw new IllegalArgumentException("Excel解析失敗：" + e.getMessage(), e);
    }
  }

  private Object convertCell(Cell cell, Class<?> paramType) {
    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue().trim();
      case NUMERIC:
        if (paramType == Long.class) {
          return (long) cell.getNumericCellValue();
        }
        if (paramType == Integer.class) {
          return (int) cell.getNumericCellValue();
        }
        if (paramType == Double.class) {
          return cell.getNumericCellValue();
        }
        return String.valueOf(cell.getNumericCellValue());
      case BOOLEAN:
        return cell.getBooleanCellValue();
      default:
        return null;
    }
  }
}
