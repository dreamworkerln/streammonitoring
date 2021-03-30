package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.*;
import ru.dreamworkerln.spring.utils.common.annotations.Default;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;
import ru.kvanttelecom.tv.streammonitoring.utils.data.StreamKey;

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
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Stream extends AbstractEntity {

    @Getter
    @Setter(AccessLevel.NONE)
    private String name;

    private String title;
    private String comment;

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
    private Point point;
    
    // Is stream online/offline
    private boolean alive;

    // Is stream flapping
    private boolean flapping;

    // Internal stream state, not stored in db
    @Getter
    @Setter
    @Transient
    private StreamState state;

    @Getter
    @Transient
    private StreamKey streamKey;

    @Default
    public Stream(String name, String title, Server server, boolean alive) {
        this.name = name;
        this.title = title;
        this.server = server;
        this.alive = alive;

        streamKey = new StreamKey(server.getName(), name);
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
