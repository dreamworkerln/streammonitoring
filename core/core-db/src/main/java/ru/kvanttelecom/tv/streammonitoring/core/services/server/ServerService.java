package ru.kvanttelecom.tv.streammonitoring.core.services.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.repositories.ServerRepository;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.BaseRepoAccessService;


import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ServerService extends BaseRepoAccessService<Server> {

    private final ServerRepository repository;


    @Autowired
    public ServerService(ServerRepository repository) {
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
     * @param domainName domainName
     */
    public Optional<Server> findByHostname(String hostname) {
        return repository.findByHostname(hostname);
    }

    /**
     * Save server
     * @return
     */
    public Server save(Server server) {
        return repository.save(server);
    }

}
