package ru.kvanttelecom.tv.streammonitoring.core.cache;


import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Aka Function<K,T>
 */
public interface CacheLevel<K, V> {

    V get(K key);

    V put(K key, V value);

    boolean containsKey(K key);

    List<V> getAll(Set<K> keys);

    List<V> getAll();
}
