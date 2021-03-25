package ru.kvanttelecom.tv.streammonitoring.utils.data;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ServerUpdate {

    @Getter
    private final String ServerName;

    @Getter
    private final List<StreamUpdate> streamUpdates;

    public ServerUpdate(String serverName) {
        ServerName = serverName;
        streamUpdates = new ArrayList<>();
    }
}
