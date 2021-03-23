package ru.kvanttelecom.tv.streammonitoring.utils.converter;

import org.mapstruct.Mapper;
import ru.kvanttelecom.tv.streammonitoring.utils.data.Stream;
import ru.kvanttelecom.tv.streammonitoring.utils.data.StreamUpdate;

import javax.annotation.PostConstruct;

@Mapper
public abstract class StreamUpdateConverter {

    @PostConstruct
    private void postConstruct() {}

    public abstract Stream toStream(StreamUpdate update);
}
