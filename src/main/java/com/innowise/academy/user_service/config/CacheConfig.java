package com.innowise.academy.user_service.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                GenericJacksonJsonRedisSerializer.builder()
                                        .enableDefaultTyping(
                                                BasicPolymorphicTypeValidator.builder()
                                                        .allowIfSubType("com.innowise.academy.user_service.dto")
                                                        .allowIfSubType("java.util")
                                                        .build())
                                        .build()
                        )
                );
    }
}