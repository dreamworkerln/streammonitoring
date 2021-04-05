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
    long streamId;

    /**
     * Event type
     */
    private Set<StreamEventType> eventSet;

    public StreamEventDto() {}

    public StreamEventDto(long streamId, Set<StreamEventType> eventSet) {
        this.streamId = streamId;
        this.eventSet = eventSet;
    }

}
