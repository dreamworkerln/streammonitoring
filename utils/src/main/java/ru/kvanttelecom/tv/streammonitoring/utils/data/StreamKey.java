package ru.kvanttelecom.tv.streammonitoring.utils.data;


import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;


public class StreamKey implements Serializable {

    @Getter
    protected String serverName;
    @Getter
    protected String streamName;

    public StreamKey() {}

    public StreamKey(String serverName, String streamName) {
        this.serverName = serverName;
        this.streamName = streamName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamKey)) return false;
        StreamKey streamKey = (StreamKey) o;
        return serverName.equals(streamKey.serverName) &&
            streamName.equals(streamKey.streamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverName, streamName);
    }

    @Override
    public String toString() {
        return "StreamKey{" +
            "serverName='" + serverName + '\'' +
            ", streamName='" + streamName + '\'' +
            '}';
    }
}
