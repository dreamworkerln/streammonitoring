package ru.kvanttelecom.tv.streammonitoring.core.dto.stream;

import lombok.Data;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;

import java.util.Set;

/**
 * Event that something changed with stream
 */
@Data
public class StreamEventDto {

    private StreamKey key;

    /**
     * Event type
     */
    private Set<StreamEventType> eventSet;

    public StreamEventDto() {}

    public StreamEventDto(StreamKey key, Set<StreamEventType> eventSet) {
        this.key = key;
        this.eventSet = eventSet;
    }

    @Override
    public String toString() {
        return "Stream '" + key + "': " + eventSet;
    }
}
