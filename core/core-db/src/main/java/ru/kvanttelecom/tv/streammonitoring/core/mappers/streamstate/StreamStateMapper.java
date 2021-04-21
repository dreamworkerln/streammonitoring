package ru.kvanttelecom.tv.streammonitoring.core.mappers.streamstate;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.dreamworkerln.spring.utils.common.Utils;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;
import ru.kvanttelecom.tv.streammonitoring.core.mappers._base.AbstractMapper;
import ru.kvanttelecom.tv.streammonitoring.core.services.caching.StreamStateMultiService;

import javax.annotation.PostConstruct;

//uses = {StreamKeyMapper.class}
@Mapper(config = AbstractMapper.class)
public abstract class StreamStateMapper extends AbstractMapper<StreamState, StreamDto> {

    @Autowired
    private StreamStateMultiService streamStateMultiService;

    @PostConstruct
    private void postConstruct() {
        this.entityAccessService = streamStateMultiService;
    }




    @Mapping(target = "flapRate", ignore = true)
    public abstract StreamState toEntity(StreamDto streamDto);


    @Mapping(target = "hostname", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "postalAddress", ignore = true)
    @Mapping(target = "coordinates", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "flapping", ignore = true)
    public abstract StreamDto toDto(StreamState streamDto);

    // Стримера не знают о наших Id, в качестве PK используется StreamKey,
    // поэтому подсосем Id вручную (если есть)

    
    @AfterMapping
    public void afterMapping(StreamDto source, @MappingTarget StreamState target) {

        streamStateMultiService.findByKey(source.getStreamKey())
            .ifPresent(l -> Utils.fieldSetter("id", target , l.getId()));
    }
}
