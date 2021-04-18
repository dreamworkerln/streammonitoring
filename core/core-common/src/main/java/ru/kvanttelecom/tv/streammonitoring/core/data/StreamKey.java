package ru.kvanttelecom.tv.streammonitoring.core.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dreamworkerln.spring.utils.common.annotations.Default;

import java.util.Objects;

@Data
public class StreamKey {
    private String hostname;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamKey)) return false;
        StreamKey streamKey = (StreamKey) o;
        return hostname.equals(streamKey.hostname) &&
            name.equals(streamKey.name);
    }

    @Default
    @JsonCreator
    public StreamKey(@JsonProperty("hostname")String hostname, @JsonProperty("name")String name) {
        this.hostname = hostname;
        this.name = name;
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
