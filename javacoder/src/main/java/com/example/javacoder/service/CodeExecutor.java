package com.example.javacoder.service;

import com.example.javacoder.model.CodeExecutionRequest;
import com.example.javacoder.model.CodeExecutionResponse;

//Kod çalıştırma işlemleri için interface

public interface CodeExecutor {
    
    //Java kodunu çalıştırır

    CodeExecutionResponse execute(CodeExecutionRequest request);
    
    //Kod çalıştırıcının desteklediği dil

    String getSupportedLanguage();
    
    //Kod çalıştırıcının durumunu kontrol eder

    boolean isReady();
} 