package com.example.javacoder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync //metodlarin asenkron çalışması icin
public class AsyncConfig {

    @Bean(name = "codeExecutionExecutor")
    public Executor codeExecutionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //aynı anda çalışabilecek minimum thread sayısı
        executor.setCorePoolSize(5);
        //max thread sayisi
        executor.setMaxPoolSize(10);
        //bekleyen istekler icin kuyruk boyutu      
        executor.setQueueCapacity(25);
        //thread isimleri için ön ek (loglarda ayırt edebilmek için kullanışlı olsun diye)
        executor.setThreadNamePrefix("CodeExec-");
        //executor'u başlatır
        executor.initialize();
        return executor;
    }
} 