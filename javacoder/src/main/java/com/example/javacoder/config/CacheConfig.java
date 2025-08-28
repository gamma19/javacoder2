package com.example.javacoder.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration  //bu sınıfın Spring configuration class olduğunu belirtir
@EnableCaching //spring'in cache mekanizmasını aktif etmek icin
public class CacheConfig {


    //expireAfterWrite, belirlenen süre sonunda cache içeriği silinir.
    //maximumSize, cache'de tutulabilecek maksimum nesne sayısı.
    @Bean
    public Caffeine<Object, Object> caffeineSpec() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000);
    }

    //burada CaffeineCacheManager kullanılıyor ve oluşturulan caffeine yapılandırması set ediliyor.
    //"supportedLanguages", "executionStats", "codeResults" -> Uygulama boyunca kullanılacak cache isimleri.
    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        //Belirli isimlerde cache'ler oluşturulur

        CaffeineCacheManager cacheManager = new CaffeineCacheManager("supportedLanguages", "executionStats", "codeResults");
        //CacheManager'e caffeine özellikleri uygulanır
        //.recordStats() ile cache hit/miss oranini gorebilirim ama kullanmadim.
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}
 