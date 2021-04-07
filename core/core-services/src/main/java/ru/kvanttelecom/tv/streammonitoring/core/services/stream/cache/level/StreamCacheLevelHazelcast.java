package ru.kvanttelecom.tv.streammonitoring.core.services.stream.cache.level;

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
//import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
//import ru.kvanttelecom.tv.streammonitoring.core.cache.CacheLevel;
//import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamKey;
//
//import javax.annotation.PostConstruct;
//import java.util.Collection;
//import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.cache.level.CacheLevelHazelcast;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;

import javax.annotation.PostConstruct;


@Component
@Order(0)
@Slf4j
public class StreamCacheLevelHazelcast extends CacheLevelHazelcast<String,Stream> {


    @PostConstruct
    private void postConstruct() {
    }


}

