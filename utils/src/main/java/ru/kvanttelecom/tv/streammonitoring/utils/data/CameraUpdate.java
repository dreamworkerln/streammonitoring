package ru.kvanttelecom.tv.streammonitoring.utils.data;

import lombok.Getter;


/**
 * Camera status update
 * (data, grabbed from flussonic media server, contained current camera status)
 */
public class CameraUpdate {

    @Getter
    private final String name;

    @Getter
    private final String serverName;

    @Getter
    private final String title;

    @Getter
    private final boolean alive;


    public CameraUpdate(String name, String serverName, String title, boolean alive) {
        this.name = name;
        this.serverName = serverName;
        this.title = title;
        this.alive = alive;
    }

    @Override
    public String toString() {
        return "CameraInfo{" +
            "name='" + name + '\'' +
            ", title='" + title + '\'' +
            ", server=" + serverName +
            ", alive=" + alive +
            '}';
    }
}
