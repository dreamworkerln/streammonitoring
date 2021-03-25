package ru.kvanttelecom.tv.streammonitoring.utils.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.dreamworkerln.spring.utils.common.annotations.Default;

import java.util.Objects;

/**
 * Stream status
 */
public class Stream {

//    @Getter
//    private StreamKey key;

    @Getter
    protected String server;

    @Getter
    protected String name;

    @Getter
    private String title;

    // Is stream online/offline
    @Getter
    @Setter
    private boolean alive;

    // Is stream flapping
    @Getter
    @Setter
    private boolean flapping;

    // Internal stream state
    @Getter
    @Setter
    @JsonIgnore
    private StreamState state = new StreamState();

    public Stream() {}

    @Default
    public Stream(String server, String name, String title, boolean alive) {
        
        this.name = name;
        this.server = server;
        this.title = title;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stream)) return false;
        Stream stream = (Stream) o;
        return server.equals(stream.server) &&
            name.equals(stream.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, name);
    }
}
