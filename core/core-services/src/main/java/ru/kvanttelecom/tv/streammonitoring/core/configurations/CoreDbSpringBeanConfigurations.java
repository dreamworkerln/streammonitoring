package ru.kvanttelecom.tv.streammonitoring.core.configurations;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;

//@EnableCaching
@Configuration
@Slf4j
public class CoreDbSpringBeanConfigurations {

    @Bean("ru.kvanttelecom.tv.streammonitoring")
    HazelcastInstance getHazelcastInstance() {
        return Hazelcast.newHazelcastInstance();
    }

}
