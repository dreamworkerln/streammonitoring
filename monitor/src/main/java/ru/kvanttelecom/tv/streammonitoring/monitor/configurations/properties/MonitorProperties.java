package ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.properties.CoreCommonProperties;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class MonitorProperties {

//    @Value("${tbot.url}")
//    @Getter
//    private String botUrl;

//    @Value("${delay.threshold.sec}")
//    @Getter
//    private int delayThresholdSec;

    @Value("${refresh.interval.sec}")
    @Getter
    private int refreshIntervalSec;


    @Value("${check.stream.uniqueness}")
    @Getter
    private boolean checkStreamUniq;

    @Getter
    @Autowired
    VideoServers servers;

    @Getter
    @Autowired
    Watcher watcher;

    @Getter
    private String protocol;

    @Autowired
    private CoreCommonProperties commonProps;

    @PostConstruct
    private void postConstruct() {

        // validating
        if(refreshIntervalSec <=0) {
            throw new IllegalArgumentException("refresh.interval.sec <=0");
        }

        protocol = commonProps.getProtocol();
    }

    @Component
    public static class VideoServers {

        /**
         * Address of origin streamer or balancer (host:port)
         */
        @Getter(AccessLevel.PUBLIC)
        @Value("#{'${media.server.list}'.split(',')}")
        private List<String> serverList;


        @Value("${media.server.username}")
        @Getter
        private String username;


        @Value("${media.server.password}")
        @Getter
        private String password;

    }

    @Component
    public static class Watcher {

        /**
         * Address of watcher (host:port)
         */
        @Getter(AccessLevel.PUBLIC)
        @Value("${watcher.address}")
        private String address;


        @Value("${watcher.username}")
        @Getter
        private String username;


        @Value("${watcher.password}")
        @Getter
        private String password;

        @Value("${watcher.token}")
        @Getter
        private String token;

    }
}
