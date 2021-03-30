package ru.kvanttelecom.tv.streammonitoring.core.cache;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Multilayer read/write-through cache
 */
@Slf4j
public class MultiCache<K,V> {

    private final List<CacheLevel<K,V>> levels;
    private final Function<K, V> cacheLoader;

    private MultiCache(MultiCacheBuilder<K,V> builder) {

        levels = builder.levels;
        cacheLoader = builder.cacheLoader;
    }


    public V get(K key) {

        V result = null;

        if(key == null) {
            return null;
        }

        // store levels without key
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

        // not found in all levels - call cacheLoader
        if(result == null) {
            result = cacheLoader.apply(key);
        }

        // result found in levels/cacheLoader
        if(result != null) {

            // write data to all lower levels
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
}
