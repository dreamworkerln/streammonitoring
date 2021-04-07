package ru.kvanttelecom.tv.streammonitoring.core.services.server;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dreamworkerln.spring.utils.common.configurations.annotations.AutowireClassList;
import ru.kvanttelecom.tv.streammonitoring.core.cache.MultiCache;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.repositories.ServerRepository;
import ru.kvanttelecom.tv.streammonitoring.core.services.server.cache.level.ServerCacheLevelDb;
import ru.kvanttelecom.tv.streammonitoring.core.services.server.cache.level.ServerCacheLevelHazelcast;


import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ServerService {

    @AutowireClassList({ServerCacheLevelHazelcast.class, ServerCacheLevelDb.class})
    private MultiCache<Long, Server> cache;

    /**
     * Get All servers.
     * <br>Truncate Lazy proxies for Server (StreamList)
     * @return
     */
    public List<Server> findAll() {
        List<Server> servers = cache.getAll();
        return servers;
    }

    /**
     * Get servers by domainName
     * <br>Truncate Lazy proxies for Server (StreamList)
     * @param domainName domainName
     */
    public Optional<Server> findByDomainName(String domainName) {
        return cache.get(domainName);
    }


    /**
     * Save server
     * @return
     */
    public Server save(Server server) {
        return repository.save(server);
    }

}
