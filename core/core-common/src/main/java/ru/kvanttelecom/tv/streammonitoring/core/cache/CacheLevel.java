package ru.kvanttelecom.tv.streammonitoring.core.cache;

/**
 * Aka Function<K,T>
 */
public interface CacheLevel<K, V> {

    V get(K key);

    V put(K key, V value);

    boolean containsKey(K key);
}
