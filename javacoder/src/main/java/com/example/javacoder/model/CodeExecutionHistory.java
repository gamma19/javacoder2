package com.example.javacoder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "code_execution_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String sourceCode;
    
    @Column(nullable = false, length = 100)
    private String className;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String input;
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String output;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String error;
    
    @Column(nullable = false)
    private long executionTime;
    
    @Column(nullable = false)
    private boolean success;
    
    @Column(nullable = false)
    private LocalDateTime executedAt;
    
    @Column(length = 64)
    private String userIp;
    
    @Column(length = 200)
    private String userAgent;
    
    @PrePersist
    protected void onCreate() {
        executedAt = LocalDateTime.now();
    }
} 