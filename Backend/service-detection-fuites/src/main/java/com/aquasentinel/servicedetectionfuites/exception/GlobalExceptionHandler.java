package com.aquasentinel.servicedetectionfuites.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> conflict(ConflictException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String msg, HttpServletRequest req) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", msg);
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
