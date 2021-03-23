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

    @Getter
    protected String name;

    @Getter
    protected String title;

    // Is stream online/offline
    @Getter
    @Setter
    protected boolean alive;

    // Is stream flapping
    @Getter
    @Setter
    protected boolean flapping;

    // Internal stream state
    @Getter
    @Setter
    @JsonIgnore
    protected StreamState state = new StreamState();

    public Stream() {}

    @Default
    public Stream(String name, String title, boolean alive) {
        this.name = name;
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
        return name.equals(stream.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
