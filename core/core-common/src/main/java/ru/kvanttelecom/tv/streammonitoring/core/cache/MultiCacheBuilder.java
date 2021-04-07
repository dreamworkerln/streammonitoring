package ru.kvanttelecom.tv.streammonitoring.core.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MultiCacheBuilder<K extends NaturalKey,V> {

    List<CacheLevel<K,V>> levels = new ArrayList<>();
    Function<K, V> cacheLoader;

    public static <K extends NaturalKey,V> MultiCacheBuilder<K,V> getBuilder() {
        return new MultiCacheBuilder<>();
    }

    private MultiCacheBuilder() {}

    public MultiCacheBuilder<K,V> addLevel(CacheLevel<K,V> level) {
        levels.add(level);
        return this;
    }

    public MultiCacheBuilder<K,V> build(Function<K, V> cacheLoader) {

        if(levels.size() == 0) {
            throw new IllegalArgumentException("levels.size() == 0. Add some levels");
        }

        if(cacheLoader == null) {
            cacheLoader = (empty) -> null;
        }
        this.cacheLoader = cacheLoader;

        return this;
    }

}
