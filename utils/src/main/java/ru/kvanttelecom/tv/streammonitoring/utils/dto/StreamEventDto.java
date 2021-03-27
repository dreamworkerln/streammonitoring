package ru.kvanttelecom.tv.streammonitoring.utils.dto;

import lombok.Data;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;

import java.util.Set;

/**
 * Event that something changed with stream
 */
@Data
public class StreamEventDto {

    /**
     * Server id
     */
    long serverId;

    /**
     * Stream name
     */
    private String name;

    /**
     * Event type
     */
    private Set<StreamEventType> eventSet;

    public StreamEventDto() {}


    public StreamEventDto(long serverId, String stream, Set<StreamEventType> eventSet) {
        this.serverId = serverId;
        this.name = name;
        this.eventSet = eventSet;
    }

}
