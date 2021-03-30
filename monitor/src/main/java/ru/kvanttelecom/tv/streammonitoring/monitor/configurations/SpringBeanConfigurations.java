package ru.kvanttelecom.tv.streammonitoring.monitor.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;

import java.time.Duration;


@Configuration
public class SpringBeanConfigurations {

    @Autowired
    MonitorProperties props;
//    @Bean
//    @Primary
//    public RestClient restClient(RestTemplate restTemplate) {
//        return new RestClient(restTemplate, props.getServers().getUsername(), props.getServers().getPassword());
//    }
}
