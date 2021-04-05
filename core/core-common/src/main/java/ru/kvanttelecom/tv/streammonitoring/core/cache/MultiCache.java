package ru.kvanttelecom.tv.streammonitoring.core.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;

/**
 * Multilayer cache that spread write to all levels
 * So L1 will be always in sync with bottom layers
 * So getAll() from L1 level will return consistent data (with low levels)
 */
@Slf4j
public class MultiCache<K,V> {

    private final List<CacheLevel<K,V>> levels;
    private final Function<K, V> cacheLoader;

    private final CacheLevel<K,V> top;

    private MultiCache(MultiCacheBuilder<K,V> builder) {
        levels = builder.levels;
        cacheLoader = builder.cacheLoader;
        top = levels.get(0);
    }


    public V get(K key) {

        V result = null;

        if(key == null) {
            return null;
        }

        // Cause altered WRITE-THROUGH, L1 will contain all entities from all bottom levels
        result = top.get(key);

        // not found in all levels - call cacheLoader(if available)
        if(result == null) {
            result = cacheLoader.apply(key);

            if(result != null) {
                // write data to all lower levels from lower to upper (in reverse order)
                put(key, result);
            }
        }

        return result;
    }


    //  Altered WRITE-THROUGH, L1 will contain all entities from all bottom levels
    public V put(K key, V value) {

        V result = value;

        // allow put with key == null
        // because lowes level may be database that assign generated Id

        if(value == null) {
            throw new IllegalArgumentException("value == null");
        }

        // ALTERED WRITE-THROUGH - WRITE ENTITY TO ALL LEVELS in backward order
        // assign write result fom previous level
        int endIndex = levels.size() - 1;
        for (int i = endIndex; i >=0 ; i--) {
            CacheLevel<K, V> level = levels.get(i);
            result = level.put(key, result);
        }
        return result;
    }


    // Batching --------------------------------------------


    public List<V> getAll(List<Long> key) {
        // Cause altered WRITE-THROUGH, L1 will contain all entities from all bottom levels
        return levels.get(0).getAll();
    }


    public List<V> getAll() {
        // Cause altered WRITE-THROUGH, L1 will contain all entities from all bottom levels
        return levels.get(0).getAll();
    }
}




/*

    public V get(K key) {

        V result = null;

        if(key == null) {
            return null;
        }

        // storing cache levels where key not found
        Deque<CacheLevel<K, V>> stack = new LinkedList<>();

        // find key in levels
        for (CacheLevel<K, V> level : levels) {

            if(level.containsKey(key)) {
                result = level.get(key);
                break;
            }
            else {
                stack.push(level);
            }
        }

        // not found in all levels - call cacheLoader(if available)
        if(result == null) {
            result = cacheLoader.apply(key);
        }

        // result found in levels/cacheLoader
        if(result != null) {
            // write data to all lower levels from lower to upper
            for (CacheLevel<K, V> level : stack) {
                stack.pop().put(key,result);
            }
        }
        return result;
    }


    public V put(K key, V value) {

        V result = value;

        if(key == null) {
            throw new IllegalArgumentException("key == null");
        }
        if(value == null) {
            throw new IllegalArgumentException("value == null");
        }

        // OVERRIDING WRITE-THROUGH
        // WILL WRITE ENTITY ON EACH LEVEL

        // write always in lower layer
        // write in layers that have key
        // also assign write result fom previous level
        int endIndex = levels.size() - 1;
        for (int i = endIndex; i >=0 ; i--) {
            CacheLevel<K, V> level = levels.get(i);
            if(level.containsKey(key) || (i == endIndex)) {
                result = level.put(key, result);
            }
        }

        return result;
    }


    // Batching --------------------------------------------

    // Cause altered WRITE-THROUGH, L1 will contain all entities from all bottom levels
    public Map<K,V> getAll(Set<StreamKey> key) {
        return levels.get(0).getAll();
    }

    // Cause altered WRITE-THROUGH, L1 will contain all entities from all bottom levels
    public Map<K,V> getAll() {
        return levels.get(0).getAll();
    }








 */