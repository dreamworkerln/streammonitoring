package ru.kvanttelecom.tv.streammonitoring.utils.dto;

import lombok.Data;
import ru.kvanttelecom.tv.streammonitoring.utils.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;

import java.util.Set;

/**
 * Event that something changed with stream
 */
@Data
public class StreamEventDto {

    /**
     * Stream name
     */
    private String name;

    /**
     * Server id
     */
    private int serverId;

    /**
     * Event type
     */
    private Set<StreamEventType> eventSet;

    public StreamEventDto() {}


    public StreamEventDto(StreamKey key, Set<StreamEventType> eventSet) {
        this.key = key;
        this.eventSet = eventSet;
    }

}
