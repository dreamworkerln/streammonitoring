package ru.kvanttelecom.tv.streammonitoring.core.repositories;


import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kvanttelecom.tv.streammonitoring.core.cache.NaturalKey;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.repositories._base.CustomRepository;
import ru.kvanttelecom.tv.streammonitoring.core.repositories.keygen.StreamNaturalKey;

import javax.persistence.PersistenceUnitUtil;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StreamRepository extends CustomRepository<Stream, Long> {

    @Override
    @Query("FROM Stream s " +
        "JOIN Server srv ON s.server  = srv " +
        "WHERE srv.hostname = :#{#key.hostName} AND s.name = :#{#key.name}")
    Optional<Stream> findByKey(NaturalKey key);


    @Query("SELECT s, " +
        "CASE WHEN COUNT(s)>0 THEN true ELSE false END " +
        "FROM Stream s " +
        "JOIN Server srv ON s.server = srv " +
        "WHERE srv.hostname = :#{#key.hostName} AND s.name = :#{#key.name} ")
    boolean existsByKey(NaturalKey key);
}


/*
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


    //Optional<Stream> findByServerNameAndName(String serverName, String name);
    @Query("FROM Stream s " +
        "JOIN Server srv ON s.server  = srv " +
        "WHERE srv.id = :#{#key.serverId} AND s.id = :#{#key.streamId}")
    List<Stream> findByStreamKey(@Param("key") StreamKey key, EntityGraph entityGraph);


    @Query("FROM Stream s " +
        "JOIN Server srv ON s.server  = srv " +
        "WHERE srv.id = :#{#key.serverId} AND s.id = :#{#key.streamId}")
    Stream findOneByStreamKey(@Param("key") StreamKey key, EntityGraph entityGraph);

    @Query("SELECT s, " +
        "CASE WHEN COUNT(s)>0 THEN true ELSE false END " +
        "FROM Stream s " +
        "JOIN Server srv ON s.server = srv " +
        "WHERE srv.id = :#{#key.serverId} AND s.id = :#{#key.streamId} ")
    boolean existsByStreamKey(StreamKey key);















*/