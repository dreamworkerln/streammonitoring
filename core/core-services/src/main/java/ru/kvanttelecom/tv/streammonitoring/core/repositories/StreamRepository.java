package ru.kvanttelecom.tv.streammonitoring.core.repositories;


import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.repositories._base.CustomRepository;
import ru.kvanttelecom.tv.streammonitoring.utils.data.StreamKey;

import java.util.Optional;
import java.util.Set;

@Repository
public interface StreamRepository extends CustomRepository<Stream, Long> {

    //Optional<Stream> findByServerNameAndName(String serverName, String name);

    @Query("FROM Stream s " +
        "JOIN Server srv ON s.server  = srv " +
        "WHERE srv.name = :#{#key.serverName} AND s.name = :#{#key.streamName}")
    Set<Stream> findByStreamKey(@Param("key") StreamKey key, EntityGraph entityGraph);


    @Query("FROM Stream s " +
        "JOIN Server srv ON s.server  = srv " +
        "WHERE srv.name = :#{#key.serverName} AND s.name = :#{#key.streamName}")
    Stream findOneByStreamKey(@Param("key") StreamKey key, EntityGraph entityGraph);

    @Query("SELECT s, " +
        "CASE WHEN COUNT(s)>0 THEN true ELSE false END " +
        "FROM Stream s " +
        "JOIN Server srv ON s.server = srv " +
        "WHERE srv.name = :#{#key.serverName} AND s.name = :#{#key.streamName} ")
    boolean existsByStreamKey(StreamKey key);
}
