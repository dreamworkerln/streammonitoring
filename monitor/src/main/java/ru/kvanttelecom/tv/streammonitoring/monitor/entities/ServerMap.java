package ru.kvanttelecom.tv.streammonitoring.monitor.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.dreamworkerln.spring.utils.common.mapwrapper.ConcurrentNavigableMapWrapper;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;
import ru.kvanttelecom.tv.streammonitoring.utils.beans.StreamMap;


import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Список стримов по серверам
 * ServerName -> StreamMap
 */
@Component
public class ServerMap extends ConcurrentNavigableMapWrapper<String, StreamMap> {

    @Autowired
    MonitorProperties props;

    @PostConstruct
    private void postConstruct() {

        // init MediaServerMap
        List<String> servers = props.getServers().getServerList();
        for (String server : servers) {
            this.put(server, new StreamMap());
        }
    }



}


