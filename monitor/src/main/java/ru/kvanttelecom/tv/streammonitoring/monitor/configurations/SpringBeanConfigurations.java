package ru.kvanttelecom.tv.streammonitoring.monitor.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.dreamworkerln.spring.utils.common.rest.RestClientBuilder;
import ru.kvanttelecom.tv.streammonitoring.core.mappers.stream.StreamMapper;
import ru.kvanttelecom.tv.streammonitoring.core.mappers.streamstate.StreamStateMapper;
import ru.kvanttelecom.tv.streammonitoring.core.services.caching.StreamMultiService;
import ru.kvanttelecom.tv.streammonitoring.core.services.caching.StreamStateMultiService;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp.stream.StreamEventSender;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.stream.StreamManager;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.downloader.StreamDownloader;

import java.time.Duration;


@Configuration
public class SpringBeanConfigurations {

    private final int WATCHER_HTTP_TIMEOUT = 20000;
    private static final String REST_TEMPLATE_WATCHER   = "rest_template_watcher";

    public static final String REST_CLIENT_MEDIASERVER = "rest_client_mediaserver";
    public static final String REST_CLIENT_WATCHER     = "rest_client_watcher";


    // Rest MediaServer

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

    // Rest Watcher

    @Bean(REST_TEMPLATE_WATCHER)
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return
            builder.setConnectTimeout(Duration.ofMillis(WATCHER_HTTP_TIMEOUT))
                .setReadTimeout(Duration.ofMillis(WATCHER_HTTP_TIMEOUT))
                .build();
    }

    @Bean(REST_CLIENT_WATCHER)
    public RestClient restClientWatcher(@Qualifier(REST_TEMPLATE_WATCHER) RestTemplate restTemplate) {

        RestClientBuilder builder = new RestClientBuilder();
        return builder
            .restTemplate(restTemplate)
            .header("x-vsaas-session", props.getWatcher().getToken())
            .userAgent("PostmanRuntime/7.26.10")
            .acceptEncoding("gzip, deflate, br")
            .build();
    }

    @Bean
    public StreamManager streamImporter(
        StreamMapper streamMapper,
        StreamStateMapper streamStateMapper,
        StreamMultiService streamMultiService,
        StreamStateMultiService streamStateMultiService,
        StreamDownloader watcherStreamDownloader,
        StreamDownloader mediaserverStreamDownloader,
        StreamEventSender streamEventSender) {

        if(props.getWatcher().isUse()) {
            return new StreamManager(streamMapper, streamStateMapper, streamMultiService, streamStateMultiService, watcherStreamDownloader, props, streamEventSender);
        }
        else {
            return new StreamManager(streamMapper, streamStateMapper, streamMultiService, streamStateMultiService, mediaserverStreamDownloader, props, streamEventSender);
        }
    }
}
