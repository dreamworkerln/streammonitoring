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


    /**
     * Уникальность имени стрима в системе(на всех стримерах)
     */
    @Value("${check.stream.global.uniqueness}")
    @Getter
    private boolean checkStreamGlobalUniq;



//    /**
//     * Уникальность имени стрима в пределах одного стримера (и так обеспечивается flussonic)
//     */
//    @Value("${check.stream.streamer.uniqueness}")
//    @Getter
//    private boolean checkStreamStreamerUniq;



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
            throw new IllegalArgumentException("refresh.interval.sec <= 0");
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
         * Использовать Watcher
         */
        @Value("${watcher.use:false}")
        @Getter
        private boolean use;

        /**
         * Address of watcher (host:port)
         */
        @Getter(AccessLevel.PUBLIC)
        @Value("${watcher.address:null}")
        private String address;


        @Value("${watcher.username:null}")
        @Getter
        private String username;


        @Value("${watcher.password:null}")
        @Getter
        private String password;

        @Value("${watcher.token:null}")
        @Getter
        private String token;

    }
}
