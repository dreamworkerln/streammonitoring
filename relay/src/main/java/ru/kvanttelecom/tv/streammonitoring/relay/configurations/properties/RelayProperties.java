package ru.kvanttelecom.tv.streammonitoring.relay.configurations.properties;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class RelayProperties {

    @Autowired
    Environment env;

    /**
     * Address of origin streamer or balancer (host:port)
     */
    @Getter(AccessLevel.PUBLIC)
    @Value("#{'${sink.url.list}'.split(',')}")
    private List<String> receiverList;


    @Getter
    private String address;





    @PostConstruct
    private void postConstruct() {
        address = env.getProperty("server.host") + ":" + env.getProperty("server.port");
    }
}
