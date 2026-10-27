package com.wordtrainer.english.controller.advice;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
    log.warn("Validation error: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
        "timestamp", LocalDateTime.now().toString(),
        "status", 400,
        "error", "Bad Request",
        "message", e.getMessage() != null ? e.getMessage() : "Invalid parameter"
    ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleException(Exception e) {
    log.error("Unhandled exception: ", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
        "timestamp", LocalDateTime.now().toString(),
        "status", 500,
        "error", "Internal Server Error",
        "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
    ));
  }
}

