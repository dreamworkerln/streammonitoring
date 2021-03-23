package ru.kvanttelecom.tv.streammonitoring.monitor.entities;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.utils.data.Stream;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Component
public class DeletedStreams {

    private static final Duration TTL = Duration.ofMinutes(1);

    private Cache<String, Stream> cache;

    @PostConstruct
    private void postConstruct() {

        cache = Caffeine.newBuilder()
            // ttl-based eviction
            .expireAfter(new Expiry<String, Stream>() {
                @Override
                public long expireAfterCreate(@NonNull String key, @NonNull Stream value, long currentTime) {
                    return TTL.toNanos();
                }

                @Override
                public long expireAfterUpdate(@NonNull String key, @NonNull Stream value, long currentTime, @NonNegative long currentDuration) {
                    return currentDuration;
                }


                /**
                 * Prolongs entry TTL on access
                 */
                @Override
                public long expireAfterRead(@NonNull String key, @NonNull Stream value, long currentTime, @NonNegative long currentDuration) {
                    return currentDuration;
                }
            })
            .build();

    }

    public Stream get(String name) {
        return cache.getIfPresent(name);
    }

    public void put(Stream stream) {
        cache.put(stream.getName(), stream);
    }


}
