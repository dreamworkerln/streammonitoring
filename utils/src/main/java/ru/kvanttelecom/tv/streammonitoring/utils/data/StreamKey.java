package ru.kvanttelecom.tv.streammonitoring.utils.data;

import lombok.Getter;

import java.util.Objects;

public class StreamKey {

    @Getter
    private final String server;
    @Getter
    private final String name;

    public StreamKey(String server, String name) {
        this.server = server;
        this.name = name;
    }

    @Override
    public String toString() {
        return "StreamKey{" +
            "server='" + server + '\'' +
            ", name='" + name + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamKey)) return false;
        StreamKey streamKey = (StreamKey) o;
        return server.equals(streamKey.server) &&
            name.equals(streamKey.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, name);
    }
}
