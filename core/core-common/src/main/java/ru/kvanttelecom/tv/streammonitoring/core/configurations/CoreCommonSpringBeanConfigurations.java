package ru.kvanttelecom.tv.streammonitoring.core.configurations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;

import java.time.Duration;


@Configuration
@Slf4j
public class CoreCommonSpringBeanConfigurations {

    private final int HTTP_TIMEOUT = 4000;

    @Primary
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return
            builder.setConnectTimeout(Duration.ofMillis(HTTP_TIMEOUT))
                .setReadTimeout(Duration.ofMillis(HTTP_TIMEOUT))
                .build();
    }

    @Primary
    @Bean
    public RestClient restClient(RestTemplate restTemplate) {
        return new RestClient(restTemplate);
    }

    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        // allow convertation to/from Instant
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        // will write as string ISO 8601
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC);
        //mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        return mapper;
    }


    @Bean("mapperWithNull")
    public ObjectMapper objectMapperIncludeNull() {

        // ObjectMapper is threadsafe

        // allow convertation to/from Instant
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        // will write as string ISO 8601
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        return mapper;
    }
}
