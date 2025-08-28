package com.example.javacoder.service;

import com.example.javacoder.model.CodeExecutionHistory;
import com.example.javacoder.model.CodeExecutionRequest;
import com.example.javacoder.model.CodeExecutionResponse;
import com.example.javacoder.repository.CodeExecutionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Kod çalıştırma işlemlerini yöneten servis sınıfı
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeExecutionService {
    
    private final List<CodeExecutor> codeExecutors;
    private final CodeExecutionHistoryRepository historyRepository;
    private final HashingService hashingService;
    
    //Java kodunu çalıştırır
    public CodeExecutionResponse executeJavaCode(CodeExecutionRequest request) {
        return executeJavaCode(request, null, null);
    }

    //Java kodunu çalıştırır ve kullanıcı meta bilgilerini kaydeder

    public CodeExecutionResponse executeJavaCode(CodeExecutionRequest request, String userIp, String userAgent) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getSourceCode() == null || request.getSourceCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Source code cannot be empty");
        }
        if (request.getClassName() == null || request.getClassName().trim().isEmpty()) {
            throw new IllegalArgumentException("Class name cannot be empty");
        }
        
        // Uygun executor'ı bul
        CodeExecutor executor = findExecutor("Java");
        if (executor == null) {
            throw new RuntimeException("Java executor not found");
        }
        
        // Kodu çalıştır
        CodeExecutionResponse response = executor.execute(request);
        
        // Geçmişe kaydet
        saveToHistory(request, response, userIp, userAgent);
        
        return response;
    }
    
    //Desteklenen dilleri döndürür

    @Cacheable("supportedLanguages")
    public List<String> getSupportedLanguages() {
        return codeExecutors.stream()
                .map(CodeExecutor::getSupportedLanguage)
                .toList();
    }
    
    // Çalıştırma geçmişini döndürür
    public Page<CodeExecutionHistory> getExecutionHistory(Pageable pageable) {
        return historyRepository.findAll(pageable);
    }
    

    //İstatistikleri döndürür
    @Cacheable("executionStats")
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long successfulExecutions = historyRepository.countSuccessfulExecutions();
        long failedExecutions = historyRepository.countFailedExecutions();
        long totalExecutions = successfulExecutions + failedExecutions; // ✅ burada tanımladık
        Double avgExecutionTime = historyRepository.getAverageExecutionTime();

        stats.put("totalExecutions", totalExecutions);
        stats.put("successfulExecutions", successfulExecutions);
        stats.put("failedExecutions", failedExecutions);
        stats.put("successRate", totalExecutions > 0
                ? (double) successfulExecutions / totalExecutions * 100
                : 0);
        stats.put("averageExecutionTime", avgExecutionTime != null ? avgExecutionTime : 0);

        return stats;
    }


    //Uygun executor'ı bulur
    private CodeExecutor findExecutor(String language) {
        return codeExecutors.stream()
                .filter(executor -> executor.getSupportedLanguage().equalsIgnoreCase(language))
                .findFirst()
                .orElse(null);
    }
    
    // Çalıştırma sonucunu geçmişe kaydeder

    private void saveToHistory(CodeExecutionRequest request, CodeExecutionResponse response, String userIp, String userAgent) {
        try {
            CodeExecutionHistory history = new CodeExecutionHistory();
            history.setSourceCode(request.getSourceCode());
            history.setClassName(request.getClassName());
            history.setInput(request.getInput());
            history.setOutput(response.getOutput());
            history.setError(response.getError());
            history.setExecutionTime(response.getExecutionTime());
            history.setSuccess(response.isSuccess());
            if (userIp != null) history.setUserIp(hashingService.hashIp(userIp));
            if (userAgent != null) history.setUserAgent(userAgent);
            
            historyRepository.save(history);
        } catch (Exception e) {
            log.error("Geçmiş kaydedilirken hata oluştu: {}", e.getMessage());
        }
    }
} 