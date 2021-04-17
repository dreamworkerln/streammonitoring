package ru.kvanttelecom.tv.streammonitoring.core.caches;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class Index<K,T> {
    private final ConcurrentMap<K,T> cache = new ConcurrentHashMap<>();

    private final Function<T,K> keyExtractor;

    // Автоматическое/ручное добавление в индекс при добавлении сущности в MapCache
    @Getter
    @Setter
    private boolean autoAddition = true;
    private boolean autoRemoval = true;  // not used yet

    public Index(Function<T, K> keyExtractor) {
        // protection from entity null values
        this.keyExtractor = t -> t != null ? keyExtractor.apply(t) : null;
    }

    public Optional<T> findByKey(K key) {
        return Optional.ofNullable(cache.get(key));
    }

    public List<T> findAll() {
        return new ArrayList<>(cache.values());
    }

    public List<T> findAllByKeys(Iterable<K> keys) {
        Set<K> keySet = new HashSet<>();
        keys.forEach(keySet::add);
        return cache.entrySet().stream().filter(e -> keySet.contains(e.getKey())).map(Map.Entry::getValue)
            .collect(Collectors.toList());
    }

    public void save(T t) {
        cache.put(keyExtractor.apply(t), t);
    }

    public void saveAll(Iterable<T> list) {
        cache.putAll(Maps.uniqueIndex(list, keyExtractor::apply));
    }

    public void delete(T t) {
        cache.remove(keyExtractor.apply(t));
    }

    public void deleteAll(Iterable<T> list) {
        for (T t : list) {
            cache.remove(keyExtractor.apply(t));
        }
    }

    public int size() {
        return cache.size();
    }

}



//        try {
//
//        }
//        catch(Exception e) {
//            log.error("Index NPE, keyExtractor: {}", keyExtractor);
//            log.error("Index NPE, t: {}", t);
//            log.error("Index NPE, key: {}", keyExtractor.apply(t));
//        }
