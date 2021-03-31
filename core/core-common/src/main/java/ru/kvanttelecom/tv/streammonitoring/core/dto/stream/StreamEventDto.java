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
     * Server id
     */
    long serverId;

    /**
     * Stream name
     */
    private StreamKey streamKey;

    /**
     * Event type
     */
    private Set<StreamEventType> eventSet;

    public StreamEventDto() {}

    public StreamEventDto(StreamKey streamKey, Set<StreamEventType> eventSet) {
        this.streamKey = streamKey;
        this.eventSet = eventSet;
    }

}
