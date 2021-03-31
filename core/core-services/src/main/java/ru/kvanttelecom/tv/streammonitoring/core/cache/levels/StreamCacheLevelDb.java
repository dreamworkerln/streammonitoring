package ru.kvanttelecom.tv.streammonitoring.core.cache.levels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.cache.CacheLevel;
import ru.kvanttelecom.tv.streammonitoring.core.repositories.StreamRepository;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamKey;

import javax.annotation.PostConstruct;


@Component
@Slf4j
@Order(1)
public class StreamCacheLevelDb implements CacheLevel<StreamKey, Stream>  {

    @PostConstruct
    private void postConstruct() {
        log.info("StreamCacheLevelDb postConstruct()");
    }

    @Autowired
    private StreamRepository repository;

    @Override
    public Stream get(StreamKey key) {
        return repository.findOneByStreamKey(key, null);
    }

    @Override
    public Stream put(StreamKey key, Stream value) {
        return repository.save(value);
    }

    @Override
    public boolean containsKey(StreamKey key) {
        return repository.existsByStreamKey(key);
    }
}
