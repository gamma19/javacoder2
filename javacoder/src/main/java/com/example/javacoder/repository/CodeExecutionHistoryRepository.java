package com.example.javacoder.repository;

import com.example.javacoder.model.CodeExecutionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CodeExecutionHistoryRepository extends JpaRepository<CodeExecutionHistory, Long> {
    
    Page<CodeExecutionHistory> findByClassNameContainingIgnoreCase(String className, Pageable pageable);
    
    Page<CodeExecutionHistory> findBySuccess(boolean success, Pageable pageable);
    
    @Query("SELECT h FROM CodeExecutionHistory h WHERE h.executedAt >= :startDate AND h.executedAt <= :endDate")
    List<CodeExecutionHistory> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(h) FROM CodeExecutionHistory h WHERE h.success = true")
    long countSuccessfulExecutions();
    
    @Query("SELECT COUNT(h) FROM CodeExecutionHistory h WHERE h.success = false")
    long countFailedExecutions();
    
    @Query("SELECT AVG(h.executionTime) FROM CodeExecutionHistory h WHERE h.success = true")
    Double getAverageExecutionTime();
    
    List<CodeExecutionHistory> findTop10ByOrderByExecutedAtDesc();
} 