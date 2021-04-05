package ru.kvanttelecom.tv.streammonitoring.core.repositories;


import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.repositories._base.CustomRepository;

import java.util.Set;

@Repository
public interface StreamRepository extends CustomRepository<Stream, Long> {



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