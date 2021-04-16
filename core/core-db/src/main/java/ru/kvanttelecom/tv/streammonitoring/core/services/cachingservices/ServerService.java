package ru.kvanttelecom.tv.streammonitoring.core.services.cachingservices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.BaseCachingService;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.Index;
import ru.kvanttelecom.tv.streammonitoring.core.services.database.ServerDatabaseService;

import java.util.Optional;

@Service
@Slf4j
public class ServerService extends BaseCachingService<Server> {

    Index<String, Server> hostnameIndex = new Index<>( Server::getHostname);
    @Autowired
    public ServerService(ServerDatabaseService serverDatabaseService) {
        super(serverDatabaseService);
        super.addIndex(hostnameIndex);
    }

    public Optional<Server> findByHostname(String hostname) {
        return Optional.ofNullable(hostnameIndex.get(hostname));
    }
}
