package com.example.javacoder.exception;

import com.example.javacoder.model.CodeExecutionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//Global exception handler,Tüm uygulama genelindeki hataları yakalar ve uygun HTTP yanıtları döndürür

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    
    //Genel exception'ları yakalar

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CodeExecutionResponse> handleGenericException(Exception ex) {
        log.error("Beklenmeyen hata: {}", ex.getMessage(), ex);
        
        CodeExecutionResponse errorResponse = new CodeExecutionResponse(
            null,
            "Sunucu hatası: " + ex.getMessage(),
            0,
            false,
            null
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    //IllegalArgumentException'ları yakalar

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CodeExecutionResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Geçersiz argüman hatası: {}", ex.getMessage());
        
        CodeExecutionResponse errorResponse = new CodeExecutionResponse(
            null,
            "Geçersiz parametre: " + ex.getMessage(),
            0,
            false,
            null
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    //Security exception'ları yakalar

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<CodeExecutionResponse> handleSecurityException(SecurityException ex) {
        log.warn("Güvenlik hatası: {}", ex.getMessage());
        
        CodeExecutionResponse errorResponse = new CodeExecutionResponse(
            null,
            "Güvenlik hatası: " + ex.getMessage(),
            0,
            false,
            null
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
} 