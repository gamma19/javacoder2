package com.example.javacoder.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    //dakikada "rpm" kadar isteğe izin verir, limit aşılırsa yeni istekler bekletilir veya reddedilir.
    @Bean
    public Bucket apiBucket(@Value("${rate.limit.requests-per-minute:100}") int rpm) {
        Bandwidth limit = Bandwidth.classic(rpm, Refill.intervally(rpm, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
 