package com.fcap.fcap_backend.error;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ApiException.class)
  ResponseEntity<ApiError> api(ApiException e) {
    return ResponseEntity.status(e.getStatus())
        .body(new ApiError(e.getStatus().value(), e.getCode(), e.getMessage(), List.of()));
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> invalid(MethodArgumentNotValidException e) {
    return ResponseEntity.unprocessableEntity()
        .body(new ApiError(422, "INVALID_INPUT", "Dữ liệu không hợp lệ", List.of()));
  }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<ApiError> badJson(HttpMessageNotReadableException e) {
    return ResponseEntity.badRequest()
        .body(new ApiError(400, "BAD_REQUEST", "JSON không đọc được", List.of()));
  }
  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> other(Exception e) {
    e.printStackTrace();   // chỉ ghi log, không trả stack trace cho client
    return ResponseEntity.status(500)
        .body(new ApiError(500, "INTERNAL_ERROR", "Lỗi hệ thống", List.of()));
  }
}