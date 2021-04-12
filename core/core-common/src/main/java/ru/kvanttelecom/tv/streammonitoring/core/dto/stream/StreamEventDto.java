package ru.kvanttelecom.tv.streammonitoring.core.dto.stream;

import lombok.Data;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;

import java.util.Set;

/**
 * Event that something changed with stream
 */
@Data
public class StreamEventDto {

    /**
     * Stream id
     */
    String hostname;
    String name;

    /**
     * Event type
     */
    private Set<StreamEventType> eventSet;

    public StreamEventDto() {}

    public StreamEventDto(String hostname, String name, Set<StreamEventType> eventSet) {
        this.hostname = hostname;
        this.name = name;
        this.eventSet = eventSet;
    }

}
