package ru.kvanttelecom.tv.streammonitoring.core.cache.levels;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.cache.CacheLevel;
import ru.kvanttelecom.tv.streammonitoring.core.repositories.StreamRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;


@Component
@Slf4j
@Order(1)
public class StreamCacheLevelDb implements CacheLevel<Long, Stream>  {

    private EntityGraph serverGraph = EntityGraphs.named(Stream.SERVER_GRAPH);


    @PostConstruct
    private void postConstruct() {
        log.info("StreamCacheLevelDb postConstruct()");
    }

    @Autowired
    private StreamRepository repository;

    @Override
    public Stream get(Long id) {
         return repository.findById(id).orElse(null);
    }
    @Override
    public boolean containsKey(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Stream put(Long key, Stream value) {
        return repository.save(value);
    }

    @Override
    public List<Stream> getAll(Set<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public List<Stream> getAll() {
        return repository.findAll();
    }
}
