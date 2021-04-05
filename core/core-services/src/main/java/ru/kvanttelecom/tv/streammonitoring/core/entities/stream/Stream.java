package ru.kvanttelecom.tv.streammonitoring.core.entities.stream;

import lombok.*;
import ru.dreamworkerln.spring.utils.common.annotations.Default;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Address;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Client;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Point;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;

import javax.persistence.*;

/**
 * Stream
 */
@Entity
@Table(
    name = "stream",
    indexes = {
    @Index(name = "stream_server_name_unq", columnList = "server_id, id", unique = true)
    })

// GRAPHS
@NamedEntityGraph(name = Stream.SERVER_GRAPH,
    attributeNodes= {@NamedAttributeNode("server")}
)

@EqualsAndHashCode(callSuper=false)
@Data
public class Stream extends AbstractEntity {

    public static final String SERVER_GRAPH = "stream.server";

    @Setter(AccessLevel.NONE)
    private String name;

    private String title;

    private String comment;

//    @Setter(AccessLevel.PACKAGE)
//    StreamKey streamKey;


    @ManyToOne
    @JoinColumn(name="server_id")
    private Server server;

    @ManyToOne
    @JoinColumn(name="address_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name="client_id")
    private Client client;

    @Embedded
    private Point coordinates;
    
    // Is stream online/offline
    private boolean alive;

    // Is stream flapping
    private boolean flapping;

    // Internal stream state, not stored in db
    @Getter
    @Setter
    @Transient
    private StreamState state;

//    @Getter
//    @Transient
//    private StreamKey streamKey;


    protected Stream() {}

    @Default
    public Stream(Server server, String name, String title, boolean alive) {
        this.server = server;
        this.name = name;
        this.title = title;
        this.alive = alive;
    }

    @Override
    public String toString() {
        return "Stream{" +
            "name='" + name + '\'' +
            ", title='" + title + '\'' +
            ", alive=" + alive +
            '}';
    }
}
