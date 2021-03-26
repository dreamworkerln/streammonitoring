package ru.kvanttelecom.tv.streammonitoring.core.converters;

import org.mapstruct.Mapper;
import ru.kvanttelecom.tv.streammonitoring.core.data.events.mediaserver.MediaServerEvent;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;

import javax.annotation.PostConstruct;

@Mapper
public abstract class MediaServerEventConverter {

    @PostConstruct
    private void postConstruct() {}

    public abstract Stream toStream(MediaServerEvent update);
}
