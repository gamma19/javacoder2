package com.example.javacoder.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Java kod çalıştırma isteği için model sınıfı
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionRequest {
    
    @NotBlank(message = "Kaynak kod boş olamaz")
    @Size(max = 10000, message = "Kaynak kod 10000 karakterden uzun olamaz")
    private String sourceCode;
    
    @NotBlank(message = "Sınıf adı boş olamaz")
    @Size(max = 100, message = "Sınıf adı 100 karakterden uzun olamaz")
    private String className;
    
    @Size(max = 1000, message = "Giriş verisi 1000 karakterden uzun olamaz")
    private String input;
} 