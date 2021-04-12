package ru.kvanttelecom.tv.streammonitoring.monitor.services.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamEventDto;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.enums.MediaServerEventType;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.events.mediaserver.MediaServerEvent;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.StreamManager;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;


@Component
@Slf4j
public class MediaServerEventHandler {

    private final Map<MediaServerEventType, Function<MediaServerEvent, Set<StreamEventType>>> handlers = new HashMap<>();

    @Autowired
    StreamStateService stateService;

    @Autowired
    StreamManager streamManager;

    @PostConstruct
    private void init() {

        handlers.put(MediaServerEventType.UNKNOWN, this::unknown);

        handlers.put(MediaServerEventType.SOURCE_READY, this::stateChanged);
        handlers.put(MediaServerEventType.SOURCE_LOST, this::stateChanged);

        handlers.put(MediaServerEventType.STREAM_STARTED, this::started);
        handlers.put(MediaServerEventType.STREAM_STOPPED, this::stopped);
    }


    /**
     * Process incoming events from Mediaserver
     * @param events incoming events
     */
    public void applyEvents(List<MediaServerEvent> events) {

        // do not handle incoming events if local streams have not been imported yet (initialized)
        if(stateService.size() == 0) {
            return;
        }

        // processed event list
        List<StreamEventDto> notifications = new ArrayList<>();

        for (MediaServerEvent event : events) {

            // calculate(process) stream events
            Set<StreamEventType> calculatedEvents = handlers.get(event.getEventType()).apply(event);

            if(calculatedEvents.size() > 0) {
                StreamEventDto dto = new StreamEventDto(event.getHostname(), event.getName(), calculatedEvents);
                notifications.add(dto);
            }
        }

        // ToDo: send events to subscribers
        if (notifications.size() > 0) {
            //log.trace("Sending events to subscribers: {}", notifications);
        }
    }

    // handlers ---------------------------------------------------------------

    private Set<StreamEventType> unknown(MediaServerEvent event) {
        log.error("UNKNOWN EVENT: {}", event);
        return new HashSet<>(Collections.singletonList(StreamEventType.ERROR));
    }

    private Set<StreamEventType> started(MediaServerEvent event) {
        log.trace("started: {}", event);

        streamManager.importOne(event.getStreamKey());
        return new HashSet<>(Collections.singletonList(StreamEventType.ADDED));
    }

    private Set<StreamEventType> stopped(MediaServerEvent event) {
        log.trace("stopped: {}", event);
        streamManager.delete(event.getStreamKey());
        return new HashSet<>(Collections.singletonList(StreamEventType.DELETED));
    }

    private Set<StreamEventType> stateChanged(MediaServerEvent event) {
        log.trace("stateChanged: {}", event);
        return stateService.process(event);
    }
}
