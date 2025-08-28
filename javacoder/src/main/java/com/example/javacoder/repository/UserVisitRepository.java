package com.example.javacoder.repository;

import com.example.javacoder.model.UserVisit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserVisitRepository extends JpaRepository<UserVisit, Long> {

    //pagination kullanilarak son ziyaretler        
    Page<UserVisit> findAllByOrderByCreatedAtDesc(Pageable pageable);

    //ip ile filtreleme
    Page<UserVisit> findByUserIpOrderByCreatedAtDesc(String userIp, Pageable pageable);

    //path içeren filtreleme
    Page<UserVisit> findByPathContainingIgnoreCaseOrderByCreatedAtDesc(String path, Pageable pageable);

    //tarih aralığında sayma
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // ip ile  yapilan en son ziyaret 
    Page<UserVisit> findTopByUserIpOrderByCreatedAtDesc(String userIp, Pageable pageable);

    List<UserVisit> findByUserIpOrderByCreatedAtDesc(String userIp);

    // En çok kullanılan tarayıcılar
    @Query("select v.userAgent as userAgent, count(v) as total from UserVisit v group by v.userAgent order by total desc")
    List<UserAgentCount> findTopUserAgents(Pageable pageable);

    // Günlük ziyaret sayıları
    @Query("select function('date', v.createdAt) as day, count(v) as total " +
            "from UserVisit v where v.createdAt between :start and :end " +
            "group by function('date', v.createdAt) order by day asc")
    List<DailyVisitCount> findDailyCounts(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    interface UserAgentCount {
        String getUserAgent();
        long getTotal();
    }

    interface DailyVisitCount {
        LocalDate getDay();
        long getTotal();
    }
}


