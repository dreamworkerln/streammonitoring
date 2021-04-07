package ru.kvanttelecom.tv.streammonitoring.core.repositories;

import org.springframework.stereotype.Repository;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.repositories._base.CustomRepository;

import javax.persistence.PersistenceUnitUtil;
import java.util.Optional;

@Repository
public interface ServerRepository extends CustomRepository<Server, Long> {

//    @Query("FROM Server srv " +
//        "JOIN Stream st ON st.server  = srv " +
//        "WHERE srv.domainName = :#{#domainName}")
    Optional<Server> findByDomainName(String domainName);


    // replace non-initialized AOP Hibernate proxy to empty Set/Map
    // for Server.streams
    default void truncateLazy(Server server) {

        if(server != null) {
            PersistenceUnitUtil unitUtil = getPersistenceUnitUtil();
            if(!unitUtil.isLoaded(server.getStreamList())) {
                server.setStreamList(null);
            }
        }
    }

}
