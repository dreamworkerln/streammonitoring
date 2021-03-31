package ru.kvanttelecom.tv.streammonitoring.tbot.entities;

import lombok.*;
import java.util.Objects;

/**
 * Stream
 */
@Data
@NoArgsConstructor
public class Stream {

    @Getter
    @Setter(AccessLevel.NONE)
    private String name;

    @Setter(AccessLevel.NONE)
    private String title;

    private String comment;

    private String server;

    // Is stream online/offline
    private boolean alive;

    // Is stream flapping
    private boolean flapping;


    @Override
    public String toString() {
        return "Stream{" +
            "server='" +  server+ '\'' +
            ", name='" + name + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stream)) return false;
        Stream stream = (Stream) o;
        return server.equals(stream.server) && name.equals(stream.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, name);
    }
}
