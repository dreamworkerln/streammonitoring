package ru.kvanttelecom.tv.streammonitoring.core.services.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.repositories.ServerRepository;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ServerService {

    @Autowired
    private ServerRepository repository;

    public List<Server> findAll() {
        return repository.findAll();
    }

    public Optional<Server> findByDomainName(String domainName) {
        return repository.findByDomainName(domainName);
    }


    public Server save(Server server) {
        return repository.save(server);
    }

}
