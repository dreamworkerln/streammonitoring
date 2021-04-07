package ru.kvanttelecom.tv.streammonitoring.core.cache.level;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.kvanttelecom.tv.streammonitoring.core.cache.CacheLevel;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class CacheLevelHazelcast<K, V> implements CacheLevel<K, V> {

    @Autowired
    @Qualifier("ru.kvanttelecom.tv.streammonitoring")
    protected HazelcastInstance cache;
    protected IMap<K, V> map;

    @PostConstruct
    private void postConstruct() {
        
        log.info(">>>>>>>>>>>> CacheLevelHazelcast has been called ");

        // configure cached map
        Config hazelcastConfig = cache.getConfig();
        String mapName = this.getClass().getSimpleName();

        MapConfig mapConfig = new MapConfig(mapName);
        hazelcastConfig.addMapConfig(mapConfig);
        map = cache.getMap(mapName);
    }
    
    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key == null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value == null");
        }
        return map.put(key, value);
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public List<V> getAll(Set<K> keys) {
        return new ArrayList<>(map.getAll(keys).values());
    }

    @Override
    public List<V> getAll() {
        return new ArrayList<>(map.values());
    }
}


/*
        //////mapConfig.setTimeToLiveSeconds(360);
        //////mapConfig.setMaxIdleSeconds(20);

//        MapLoader<StreamKey, Stream> testMapLoader = new MapLoader<>() {
//            @Override
//            public Stream load(StreamKey key) {
//                return null;
//            }
//
//            @Override
//            public Map<StreamKey, Stream> loadAll(Collection keys) {
//                return null;
//            }
//
//            @Override
//            public Iterable<StreamKey> loadAllKeys() {
//                return null;
//            }
//        };
//        MapStoreConfig mapStoreConfig = new MapStoreConfig().setImplementation(testMapLoader);
//        mapConfig.setMapStoreConfig(mapStoreConfig);

//////        mapConfig.getMapStoreConfig().setFactoryImplementation()
//////        MapConfig mapConfig = hazelcastConfig.getMapConfig(getClass().getName()).setMapStoreConfig(mapStoreConfig);
// MethodHandles.lookup().lookupClass().getSimpleName();
 */
