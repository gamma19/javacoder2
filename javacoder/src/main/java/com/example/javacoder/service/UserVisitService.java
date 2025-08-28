package com.example.javacoder.service;

import com.example.javacoder.model.UserVisit;
import com.example.javacoder.repository.UserVisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserVisitService {

    private final UserVisitRepository repository;
    private final HashingService hashingService;

    public UserVisit saveVisit(String userIp, String userAgent, String path) {
        UserVisit visit = new UserVisit();
        String hashedIp = hashingService.hashIp(userIp);
        visit.setUserIp(hashedIp);
        visit.setUserAgent(userAgent);
        visit.setPath(path);
        return repository.save(visit);
    }
}


