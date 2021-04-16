package ru.kvanttelecom.tv.streammonitoring.core.services.database;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.repositories.ServerRepository;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.BaseRepoAccessService;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@Slf4j
public class ServerDatabaseService extends BaseRepoAccessService<Server> {

    private final ServerRepository repository;
                  // <Hostname, Server>
    private final Map<String, Server> cacheHost = new ConcurrentHashMap<>();

    @Autowired
    public ServerDatabaseService(ServerRepository repository) {
        super(repository);
        this.repository = repository;
    }

    /**
     * Get servers by domainName
     * @param domainName domainName
     */
    public Optional<Server> findByDomainName(String domainName) {
        return repository.findByDomainName(domainName);
    }

    /**
     * Get server by domainName
     * @param hostname server.hostname
     */
    public Optional<Server> findByHostname(String hostname) {
        return repository.findByHostname(hostname);
    }

    /**
     * Save server
     * @return persisted/updated server
     */
    public Server save(Server server) {
        Server result = repository.save(server);
        return result;
    }
}
