package ru.kvanttelecom.tv.streammonitoring.utils.dto;

import lombok.Data;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;

import java.util.Set;

/**
 * Event that something changed with stream
 */
@Data
public class StreamEvent {

    /**
     * Stream name - id
     */
    private String name;

    /**
     * Event type
     */
    private Set<StreamEventType> eventSet;

    public StreamEvent() {}


    public StreamEvent(String name, Set<StreamEventType> eventSet) {
        this.name = name;
        this.eventSet = eventSet;
    }

}
