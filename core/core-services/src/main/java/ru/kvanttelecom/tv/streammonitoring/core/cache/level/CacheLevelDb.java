package ru.kvanttelecom.tv.streammonitoring.core.cache.level;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kvanttelecom.tv.streammonitoring.core.cache.CacheLevel;
import ru.kvanttelecom.tv.streammonitoring.core.cache.NaturalKey;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.repositories.StreamRepository;
import ru.kvanttelecom.tv.streammonitoring.core.repositories._base.CustomRepository;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CacheLevelDb<K extends NaturalKey, V> implements CacheLevel<K, V> {

    protected EntityGraph graph;

    private CustomRepository<V, Long> repository;

    @Override
    public V get(K key) {

        Optional<V> value = repository.findByKey(key);
        value.ifPresent(v -> repository.truncateLazy(v));
        return value.orElse(null);

    }

    @Override
    public boolean containsKey(K key) {
        return repository.existsByKey(key);
    }

    @Override
    public V put(K key, V value) {
        return repository.save(value);
    }

    @Override
    public List<V> getAll(Set<K> ids) {
        return repository.findAllById(ids); 
    }

    @Override
    public List<V> getAll() {
        return repository.findAll();
    }

    //repository.findById(id.get()).orElse(null);
}
