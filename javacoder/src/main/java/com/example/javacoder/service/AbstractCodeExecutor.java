package com.example.javacoder.service;

import com.example.javacoder.model.CodeExecutionRequest;
import com.example.javacoder.model.CodeExecutionResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Kod çalıştırma işlemleri için abstract base class
 * Template Method pattern kullanarak ortak işlemleri tanımlar
 */
@Slf4j
public abstract class AbstractCodeExecutor implements CodeExecutor {
    
    protected static final String TEMP_DIR = "temp";
    protected static final String JAVA_EXTENSION = ".java";
    protected static final String CLASS_EXTENSION = ".class";
    
    @Override
    public CodeExecutionResponse execute(CodeExecutionRequest request) {
        long startTime = System.currentTimeMillis();
        CodeExecutionResponse response = new CodeExecutionResponse();
        
        try {
            // Template method pattern - alt sınıflar implement eder
            response = doExecute(request);
            response.setSuccess(true);
        } catch (Exception e) {
            log.error("Kod çalıştırma hatası: {}", e.getMessage(), e);
            response.setError("Çalıştırma hatası: " + e.getMessage());
            response.setSuccess(false);
        } finally {
            response.setExecutionTime(System.currentTimeMillis() - startTime);
            response.setClassName(request.getClassName());
        }
        
        return response;
    }
    
    //alt sınıfların implement etmesi gereken abstract method

    protected abstract CodeExecutionResponse doExecute(CodeExecutionRequest request) throws Exception;
    
    // gecici dosya olusturma
    protected Path createTempFile(String prefix, String suffix) throws IOException {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("Dosya ön eki boş olamaz");
        }
        
        ensureTempDirectoryExists();
        // Benzersiz bir alt dizin oluştur
        String uniqueDir = UUID.randomUUID().toString().substring(0, 8);
        Path uniquePath = Paths.get(TEMP_DIR, uniqueDir);
        Files.createDirectories(uniquePath);
        
        // Sınıf adı ile dosya adı aynı olmalı (Java kuralı)
        String fileName = prefix + suffix;
        Path filePath = uniquePath.resolve(fileName);
        
        log.debug("Geçici dosya oluşturuluyor: {}", filePath);
        return filePath;
    }
    
    /**
     * Geçici dizinin varlığını kontrol eder ve gerekirse oluşturur
     * @throws IOException Dizin oluşturma hatası
     */
    protected void ensureTempDirectoryExists() throws IOException {
        Path tempPath = Paths.get(TEMP_DIR);
        if (!Files.exists(tempPath)) {
            Files.createDirectories(tempPath);
        }
    }
    
    /**
     * Geçici dosyaları temizler
     * @param files Temizlenecek dosyalar
     */
    protected void cleanupTempFiles(Path... files) {
        for (Path file : files) {
            try {
                if (file != null && Files.exists(file)) {
                    Files.delete(file);
                    log.debug("Geçici dosya silindi: {}", file);
                }
            } catch (IOException e) {
                log.warn("Geçici dosya silinemedi: {}", file, e);
            }
        }
    }
    
    /**
     * Geçici dizini temizler
     * @param directory Temizlenecek dizin
     */
    protected void cleanupDirectory(Path directory) {
        try {
            if (directory != null && Files.exists(directory)) {
                // Dizindeki tüm dosyaları sil
                Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a)) // Alt dizinlerden başla
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            log.debug("Dosya/dizin silindi: {}", path);
                        } catch (IOException e) {
                            log.warn("Dosya/dizin silinemedi: {}", path, e);
                        }
                    });
            }
        } catch (IOException e) {
            log.warn("Dizin temizleme hatası: {}", directory, e);
        }
    }
    
    @Override
    public boolean isReady() {
        try {
            ensureTempDirectoryExists();
            return true;
        } catch (IOException e) {
            log.error("Kod çalıştırıcı hazır değil: {}", e.getMessage());
            return false;
        }
    }
} 