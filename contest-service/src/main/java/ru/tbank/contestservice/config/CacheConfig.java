package ru.tbank.contestservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CaffeineCache languageCache(ApplicationConfig applicationConfig) {
        return new CaffeineCache(
                applicationConfig.cache().language().cacheName(),
                Caffeine.newBuilder()
                        .expireAfterWrite(applicationConfig.cache().language().expireAfterWrite())
                        .recordStats()
                        .build()
        );
    }

    @Bean
    public CacheResolver languageCacheResolver(Cache languageCache) {
        return context -> List.of(languageCache);
    }

}
