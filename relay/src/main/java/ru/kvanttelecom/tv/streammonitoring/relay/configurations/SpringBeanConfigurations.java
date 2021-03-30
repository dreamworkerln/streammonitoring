package ru.kvanttelecom.tv.streammonitoring.relay.configurations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.dreamworkerln.spring.utils.common.threadpool.BlockingJobPool;

import java.time.Duration;


@Configuration
@Slf4j
public class SpringBeanConfigurations {

    @Bean
    BlockingJobPool<Void,Void> jobPool() {
        return new BlockingJobPool<>(10, Duration.ofSeconds(1), jr -> {
            if(jr.getException() != null) {
                log.error("JOB ERROR:", jr.getException());
            }
        });
    }


}
