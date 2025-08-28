package com.example.javacoder.controller;

import com.example.javacoder.model.CodeExecutionRequest;
import com.example.javacoder.model.CodeExecutionResponse;
import com.example.javacoder.service.CodeExecutionService;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CORS desteği ile frontend'den gelen istekleri karşılar

@Slf4j //otomatik logger tanımlar (log.info, log.error kullanılabilir)
@RestController
@RequestMapping("/api/code")
@RequiredArgsConstructor // final field'lar için constructor injection üretir ya da @NonNull lar icin.
@CrossOrigin(origins = "http://localhost:3000") //cors sadece buradan cagirilabilir.
public class CodeExecutionController {
    
    private final CodeExecutionService codeExecutionService;
    

    //burada kullanicidan gelen java kodunu calistiriyoruz
    @PostMapping("/execute/java")
    @Timed(value = "code.execution.time", description = "Time taken to execute Java code")
    public ResponseEntity<CodeExecutionResponse> executeJavaCode(
            @Valid @RequestBody CodeExecutionRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Java kodu çalıştırma isteği alındı. Sınıf: {}", 
                request.getClassName());
        
        try {
            // Kullanıcı bilgilerini al (IP, User-Agent)
            String ip = getClientIpAddress(httpRequest);
            String agent = httpRequest.getHeader("User-Agent");

            // Servis katmanında kodu çalıştır
            CodeExecutionResponse response = codeExecutionService.executeJavaCode(request, ip, agent);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
             // Hata logla ve kullanıcıya generic error response dön
            log.error("Kod çalıştırma hatası: {}", e.getMessage(), e);
            CodeExecutionResponse errorResponse = new CodeExecutionResponse(
                null,
                "Sunucu hatası: " + e.getMessage(),
                0,
                false,
                request.getClassName()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    //desteklenen diller
    @GetMapping("/languages")
    @Cacheable("supportedLanguages")
    @Timed(value = "languages.request.time", description = "Time taken to get supported languages")
    public ResponseEntity<List<String>> getSupportedLanguages() {
        List<String> languages = codeExecutionService.getSupportedLanguages();
        return ResponseEntity.ok(languages);
    }
    
    //saglik kontrolu
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Java Code Executor is running!");
    }
    
    //Kod çalıştırma geçmişini döndürür
    @GetMapping("/history")
    public ResponseEntity<Page<?>> getExecutionHistory(Pageable pageable) {
        Page<?> history = codeExecutionService.getExecutionHistory(pageable);
        return ResponseEntity.ok(history);
    }
    
    //istatistikler
    @GetMapping("/stats")
    @Cacheable("executionStats")
    public ResponseEntity<?> getStatistics() {
        return ResponseEntity.ok(codeExecutionService.getStatistics());
    }
    
    //client IP'sini alir
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
} 