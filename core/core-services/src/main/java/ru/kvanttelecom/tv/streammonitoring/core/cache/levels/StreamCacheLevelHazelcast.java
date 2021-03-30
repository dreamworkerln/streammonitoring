package ru.kvanttelecom.tv.streammonitoring.core.cache.levels;

//import com.hazelcast.config.Config;
//import com.hazelcast.config.MapConfig;
//import com.hazelcast.config.MapStoreConfig;
//import com.hazelcast.core.HazelcastInstance;
//import com.hazelcast.map.IMap;
//import com.hazelcast.map.MapLoader;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.ApplicationContext;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;
//import ru.kvanttelecom.tv.streammonitoring.core.cache.CacheLevel;
//import ru.kvanttelecom.tv.streammonitoring.utils.data.StreamKey;
//
//import javax.annotation.PostConstruct;
//import java.util.Collection;
//import java.util.Map;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.MapLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.cache.CacheLevel;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;
import ru.kvanttelecom.tv.streammonitoring.utils.data.StreamKey;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;


@Component
@Order(0)
@Slf4j
public class StreamCacheLevelHazelcast implements CacheLevel<StreamKey, Stream> {


    @Autowired
    @Qualifier("myHazelcast")
    private HazelcastInstance cache;

    private IMap<StreamKey, Stream> map;


    @PostConstruct
    private void postConstruct() {

        // configure cached map
        Config hazelcastConfig = cache.getConfig();
        String mapName = this.getClass().getSimpleName(); // MethodHandles.lookup().lookupClass().getSimpleName();
        
        MapConfig mapConfig = new MapConfig(mapName);
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

        hazelcastConfig.addMapConfig(mapConfig);
        map = cache.getMap(mapName);
    }


    @Override
    public Stream get(StreamKey key) {
        return map.get(key);
    }

    @Override
    public Stream put(StreamKey key, Stream value) {
        return map.put(key, value);
    }

    @Override
    public boolean containsKey(StreamKey key) {
        return map.containsKey(key);
    }
}
