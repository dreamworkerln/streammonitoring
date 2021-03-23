package ru.kvanttelecom.tv.streammonitoring.utils.dto;

import lombok.Data;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.CameraEventType;

import java.util.Set;

/**
 * Event that something changed with camera
 */
@Data
public class StreamEvent {

    /**
     * Camera name - id
     */
    private String name;

    /**
     * Event type
     */
    private Set<CameraEventType> eventSet;

    public StreamEvent() {}


    public StreamEvent(String name, Set<CameraEventType> eventSet) {
        this.name = name;
        this.eventSet = eventSet;
    }

}
