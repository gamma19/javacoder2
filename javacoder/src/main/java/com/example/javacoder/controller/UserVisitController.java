package com.example.javacoder.controller;

import com.example.javacoder.model.UserVisit;
import com.example.javacoder.service.UserVisitService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/visit")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserVisitController {

    private final UserVisitService userVisitService;

    @PostMapping
    public ResponseEntity<UserVisit> saveVisit(@RequestParam(required = false) String path,
                                               HttpServletRequest request) {
        String ip = getClientIpAddress(request);
        String agent = request.getHeader("User-Agent");
        UserVisit saved = userVisitService.saveVisit(ip, agent, path != null ? path : "/");
        return ResponseEntity.ok(saved);
    }

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


