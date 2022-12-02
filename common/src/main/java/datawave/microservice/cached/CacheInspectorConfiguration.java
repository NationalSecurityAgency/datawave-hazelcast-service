package datawave.microservice.cached;

import com.hazelcast.spring.cache.HazelcastCacheManager;
import datawave.autoconfigure.DatawaveCacheAutoConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.function.Function;

/**
 * Provides an instance of a {@link CacheInspector}.
 */
@Configuration
public class CacheInspectorConfiguration {
    
    @Bean
    public Function<CacheManager,CacheInspector> cacheInspectorFactory() {
        return cacheManager -> {
            if (cacheManager instanceof HazelcastCacheManager)
                return new HazelcastCacheInspector(cacheManager);
            else if (cacheManager instanceof CaffeineCacheManager)
                return new CaffeineCacheInspector(cacheManager);
            else if (cacheManager instanceof ConcurrentMapCacheManager)
                return new ConcurrentMapCacheInspector(cacheManager);
            else
                return new UnsupportedOperationCacheInspector();
        };
    }
    
    private static class UnsupportedOperationCacheInspector implements CacheInspector {
        @Override
        public <T> T list(String cacheName, Class<T> cacheObjectType, String key) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public <T> List<? extends T> listAll(String cacheName, Class<T> cacheObjectType) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public <T> List<? extends T> listMatching(String cacheName, Class<T> cacheObjectType, String substring) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public <T> int evictMatching(String cacheName, Class<T> cacheObjectType, String substring) {
            throw new UnsupportedOperationException();
        }
    }
}
