package com.example.javacoder.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Java kod çalıştırma sonucu için model sınıfı
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionResponse {
    private String output;
    private String error;
    private long executionTime;
    private boolean success;
    private String className;
} 