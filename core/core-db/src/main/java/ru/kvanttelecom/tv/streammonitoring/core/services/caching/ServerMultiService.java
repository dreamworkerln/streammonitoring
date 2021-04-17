package ru.kvanttelecom.tv.streammonitoring.core.services.caching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.caches.Cachelevel;
import ru.kvanttelecom.tv.streammonitoring.core.caches.MapCache;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.Multicache;
import ru.kvanttelecom.tv.streammonitoring.core.caches.Index;
import ru.kvanttelecom.tv.streammonitoring.core.services.database.ServerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ServerMultiService extends Multicache<Server> {

    private final Index<String, Server> hostnameIndex = new Index<>(Server::getHostname);

    @Autowired
    private ServerService serverService;

    public Optional<Server> findByHostname(String hostname) {
        return hostnameIndex.findByKey(hostname);
    }

    @Override
    protected List<Cachelevel<Server>> addLevels() {

        MapCache<Server> mapCache = new MapCache<>();
        mapCache.addIndex(hostnameIndex);
        return new ArrayList<>(List.of(mapCache, serverService));
    }
}
