package ru.kvanttelecom.tv.streammonitoring.utils.data;

import lombok.Getter;


/**
 * Stream status update
 * (data, grabbed from flussonic media server, contained current stream status)
 */
public class StreamUpdate {

    @Getter
    private final String name;

    @Getter
    private final String serverName;

    @Getter
    private final String title;

    @Getter
    private final boolean alive;


    public StreamUpdate(String name, String serverName, String title, boolean alive) {
        this.name = name;
        this.serverName = serverName;
        this.title = title;
        this.alive = alive;
    }

    @Override
    public String toString() {
        return "StreamUpdate{" +
            "name='" + name + '\'' +
            ", title='" + title + '\'' +
            ", server=" + serverName +
            ", alive=" + alive +
            '}';
    }
}
