package ru.kvanttelecom.tv.streammonitoring.core.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
public class StreamKey {
    private final String hostname;
    private final String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamKey)) return false;
        StreamKey streamKey = (StreamKey) o;
        return hostname.equals(streamKey.hostname) &&
            name.equals(streamKey.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname, name);
    }

    @Override
    public String toString() {
        return hostname + "." + name;
    }
}
