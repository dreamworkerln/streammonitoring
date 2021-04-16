package ru.kvanttelecom.tv.streammonitoring.core.services._base;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class Index<K,T> {
    private final ConcurrentMap<K,T> cache = new ConcurrentHashMap<>();

    private final Function<T,K> keyExtractor;

    public Index(Function<T, K> keyExtractor) {
        // protection from entity null values
        this.keyExtractor = t -> t != null ? keyExtractor.apply(t) : null;
    }

    public T get(K key) {
        return cache.get(key);
    }

    public List<T> getByKeys(Iterable<K> keys) {
        Set<K> keySet = new HashSet<>();
        keys.forEach(keySet::add);

        return cache.entrySet().stream().filter(e -> keySet.contains(e.getKey())).map(Map.Entry::getValue)
            .collect(Collectors.toList());
    }

    public List<T> values() {
        return new ArrayList<>(cache.values());
    }

    public int size() {
        return cache.size();
    }

    public void put(T t) {
        cache.put(keyExtractor.apply(t), t);
    }

    public void putAll(List<T> list) {
        cache.putAll(list.stream().collect(Collectors.toMap(keyExtractor, Function.identity())));
    }
    public void remove(T t) {

        try {
            cache.remove(keyExtractor.apply(t));
        }
        catch(Exception e) {
            log.error("Index NPE, keyExtractor: {}", keyExtractor);
            log.error("Index NPE, t: {}", t);
            log.error("Index NPE, key: {}", keyExtractor.apply(t));
        }
    }

    /**
     * Initialize index cache from database.
     * Call only one time on app startup.
     */
    public void init(List<T> list) {
        putAll(list);
    }
}


//    /**
//     * Find entity by key
//     * <br> Backed by database (cacheloader calling DB)
//     */
//    public Optional<T> findByKey(K key) {
//        // get from cache
//        Optional<T> result = Optional.ofNullable(cache.get(key));
//        // not found in cache
//        if(result.isEmpty()) {
//            result = findByKey.apply(key);
//            // if found in DB then save to cache
//            result.ifPresent(this::put);
//        }
//        return result;
//    }


//    public List<T> findAll() {
//        // return cache values
//        return new ArrayList<>(cache.values());
//    }
//
//
//    public List<T> findAllByKey(Iterable<K> keys) {
//        // find in cache by id
//        return idIndex.getByKeys(listId);
//    }




//    protected Optional<T> findByIndex(Object key, Index<?, T> index) {
//
//        // get from cache
//        Optional<T> result = Optional.ofNullable(index.get(key));
//        // not found in cache
//        if(result.isEmpty()) {
//            result = indexRepositoryLoaders.get(index.getName()).apply(key);
//            // if found in DB then save to cache
//            result.ifPresent(index::put);
//        }
//
