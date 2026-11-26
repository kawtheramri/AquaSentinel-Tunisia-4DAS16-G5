package com.example.userservice.exception;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
@RestControllerAdvice
public class GlobalExceptionHandler{
 public record ErrorBody(LocalDateTime timestamp,int status,String error,String message,String path){}
 @ExceptionHandler(ApiException.class) ResponseEntity<ErrorBody> api(ApiException e,HttpServletRequest r){return response(e.getStatus(),e.getMessage(),r);}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException e,HttpServletRequest r){String m=e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).collect(Collectors.joining("; "));return response(HttpStatus.BAD_REQUEST,m,r);}
 @ExceptionHandler({IllegalArgumentException.class,ConstraintViolationException.class}) ResponseEntity<ErrorBody> bad(RuntimeException e,HttpServletRequest r){return response(HttpStatus.BAD_REQUEST,e.getMessage(),r);}
 @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class}) ResponseEntity<ErrorBody> malformed(Exception e,HttpServletRequest r){return response(HttpStatus.BAD_REQUEST,"Requête ou paramètre invalide",r);}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<ErrorBody> integrity(DataIntegrityViolationException e,HttpServletRequest r){return response(HttpStatus.CONFLICT,"Conflit avec une donnée existante",r);}
 @ExceptionHandler(Exception.class) ResponseEntity<ErrorBody> other(Exception e,HttpServletRequest r){return response(HttpStatus.INTERNAL_SERVER_ERROR,"Erreur interne",r);}
 private ResponseEntity<ErrorBody> response(HttpStatus s,String m,HttpServletRequest r){return ResponseEntity.status(s).body(new ErrorBody(LocalDateTime.now(),s.value(),s.getReasonPhrase(),m==null?s.getReasonPhrase():m,r.getRequestURI()));}
}
