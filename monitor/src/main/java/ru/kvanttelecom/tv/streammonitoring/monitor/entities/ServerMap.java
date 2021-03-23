package ru.kvanttelecom.tv.streammonitoring.monitor.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.dreamworkerln.spring.utils.common.mapwrapper.ConcurrentNavigableMapWrapper;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;


import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Список камер по серверам
 * ServerName -> CameraMap
 */
@Component
public class ServerMap extends ConcurrentNavigableMapWrapper<String, CameraMap> {

    @Autowired
    MonitorProperties props;

    @PostConstruct
    private void postConstruct() {

        // init MediaServerMap
        List<String> servers = props.getServers().getServerList();
        for (String server : servers) {
            this.put(server, new CameraMap());
        }
    }



}


