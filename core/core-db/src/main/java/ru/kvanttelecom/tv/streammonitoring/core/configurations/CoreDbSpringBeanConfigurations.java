package ru.kvanttelecom.tv.streammonitoring.core.configurations;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CoreDbSpringBeanConfigurations {

    @Bean("myHazelcast")
    HazelcastInstance getHazelcastInstance() {
        return Hazelcast.newHazelcastInstance();
    }

}
