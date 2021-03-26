package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.Getter;
import ru.kvanttelecom.tv.streammonitoring.core.entities.base.AbstractEntity;

public class Server extends AbstractEntity {

    @Getter
    private String name;

    @Getter
    private String url;

    public Server(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
