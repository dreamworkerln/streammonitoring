package ru.kvanttelecom.tv.streammonitoring.core.services.stream.cache.level;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.cache.level.CacheLevelDb;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.repositories.keygen.StreamNaturalKey;

import javax.annotation.PostConstruct;


@Component
@Slf4j
@Order(1)
public class StreamCacheLevelDb extends CacheLevelDb<String,Stream> {

    @PostConstruct
    private void postConstruct() {
        graph = EntityGraphs.named(Stream.SERVER_GRAPH);
    }

    boolean existsByKey(StreamNaturalKey key) {}



}
