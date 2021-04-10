package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.util.Assert;
import ru.dreamworkerln.spring.utils.common.annotations.Default;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Stream
 */
@Entity

@Table(
    name = "stream",
    indexes = {
        @Index(name = "stream_name_index", columnList = "name"),
        @Index(name = "stream_server_name_name_unq", columnList = "server_id, name", unique = true)
    })
@NamedEntityGraph(name = Stream.STREAM_ADDRESS,
    attributeNodes= {@NamedAttributeNode("address")}
)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@Slf4j
public class Stream extends AbstractEntity {

    //public static final String SERVER_GRAPH = "stream.server";
    public static final String STREAM_ADDRESS = "stream.address";

    @Setter(AccessLevel.NONE)
    private String name;

    private String title;

    private String comment;

//    @Setter(AccessLevel.PACKAGE)
//    StreamKey streamKey;


    @ManyToOne
    @JoinColumn(name="server_id")
    private Server server;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="address_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name="client_id")
    private Client client;

    //@Embedded
    //private Point coordinates;


    // was stream alive in the moment when it was imported to the system - for internal use only
    // To get current stream status use StreamStatusService
    @Transient
    private boolean initialStateAlive;

    // Is stream flapping
    //private boolean flapping;

    // Internal stream state, not stored in db
    //@Getter
    //@Setter
    //@Transient
    //private StreamState state;

//    @Getter
//    @Transient
//    private StreamKey streamKey;


    protected Stream() {}

    @Default
    public Stream(Server server, String name, String title) {
        this.server = server;
        this.name = name;
        this.title = title;
    }

    public String getStreamKey() {
        Assert.notNull(server, "Stream.server == null");
        return server.getHostname() + "." + name;
    }

    @Override
    public String toString() {
        return "Stream{" +
            "name='" + name + '\'' +
            ", title='" + title + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stream)) return false;
        Stream stream = (Stream) o;
        Assert.notNull(server, "Stream.server == null");
        return name.equals(stream.name) &&
            Objects.equals(title, stream.title) &&
            Objects.equals(comment, stream.comment) &&
            Objects.equals(server, stream.server) &&
            Objects.equals(address, stream.address) &&
            Objects.equals(client, stream.client);
    }

    @Override
    public int hashCode() {
        Assert.notNull(server, "Stream.server == null");
        return Objects.hash(name, title, comment, server, address, client);
    }
}
