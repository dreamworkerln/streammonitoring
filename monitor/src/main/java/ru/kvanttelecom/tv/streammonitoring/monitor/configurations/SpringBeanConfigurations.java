package ru.kvanttelecom.tv.streammonitoring.monitor.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.dreamworkerln.spring.utils.common.rest.RestClientBuilder;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;


@Configuration
public class SpringBeanConfigurations {

    public static final String REST_CLIENT_MEDIASERVER = "mediaserver";
    public static final String REST_CLIENT_WATCHER = "watcher";
    @Autowired
    MonitorProperties props;

    @Bean(REST_CLIENT_MEDIASERVER)
    public RestClient restClientMediaServer(RestTemplate restTemplate) {

        RestClientBuilder builder = new RestClientBuilder();
        return builder
            .restTemplate(restTemplate)
            .basicAuth(props.getServers().getUsername(), props.getServers().getPassword())
            .build();
    }

    @Bean(REST_CLIENT_WATCHER)
    public RestClient restClientWatcher(RestTemplate restTemplate) {

        RestClientBuilder builder = new RestClientBuilder();
        return builder
            .restTemplate(restTemplate)
            .header("x-vsaas-session", props.getWatcher().getToken())
            .build();
    }
}
