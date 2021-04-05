package ru.kvanttelecom.tv.streammonitoring.core.repositories;

import org.springframework.stereotype.Repository;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.repositories._base.CustomRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRepository extends CustomRepository<Server, Long> {

    Optional<Server> findByDomainName(String domainName);

}
