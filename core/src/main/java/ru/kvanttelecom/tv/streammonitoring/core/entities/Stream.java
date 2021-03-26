package ru.kvanttelecom.tv.streammonitoring.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.math3.analysis.function.Add;
import ru.dreamworkerln.spring.utils.common.annotations.Default;
import ru.kvanttelecom.tv.streammonitoring.core.data.Point;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.entities.base.AbstractEntity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Stream
 */
@Entity
@Table(name = "stream")
//@EqualsAndHashCode(callSuper=true)
@Data
@NoArgsConstructor
public class Stream extends AbstractEntity {

    @Getter
    @Setter(AccessLevel.NONE)
    private String name;

    private String title;
    private String comment;
    private Server server;

    @ManyToOne
    @JoinColumn(name="client_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name="client_id")
    private Client owner;


    private Point point;
    
    // Is stream online/offline
    private boolean alive;

    // Is stream flapping
    private boolean flapping;

    // Internal stream state
    @Getter
    @Setter
    @Transient
    private StreamState state = new StreamState();

    @Default
    public Stream(String name, String title, Server server, boolean alive) {
        
        this.name = name;
        this.title = title;
        this.server = server;
        this.alive = alive;
        state.setLastUpdateAlive(alive);
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
