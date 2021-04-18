package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.eventhandlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.enums.MediaServerEventType;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.events.mediaserver.MediaServerEvent;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.StreamManager;
import ru.kvanttelecom.tv.streammonitoring.core.services.caching.StreamStateMultiService;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Consumer;


@Component
@Slf4j
public class MediaServerEventHandler {

    // Set<StreamEventType>
    private final Map<MediaServerEventType, Consumer<MediaServerEvent>> handlers = new HashMap<>();

    @Autowired
    StreamStateMultiService stateService;

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

        //List<StreamEventDto> notifications = new ArrayList<>();

        // process all events from mediaserver
        for (MediaServerEvent event : events) {
            handlers.get(event.getEventType()).accept(event);
        }

    }

    // handlers ---------------------------------------------------------------

    private void unknown(MediaServerEvent event) {
        log.error("UNKNOWN EVENT: {}", event);
        //return new HashSet<>(Collections.singletonList(StreamEventType.ERROR));
    }

    // Stream has been started on mediaserver
    private void started(MediaServerEvent event) {
        log.trace("Started: '{}'", event.getStreamKey());

        streamManager.start(event.getStreamKey());
        //return new HashSet<>(Collections.singletonList(StreamEventType.ADDED));
    }

    // Stream has been stopped on mediaserver
    private void stopped(MediaServerEvent event) {
        log.trace("Stopped: '{}'", event.getStreamKey());
        streamManager.stop(event.getStreamKey());
        //return new HashSet<>(Collections.singletonList(StreamEventType.DELETED));
    }

    // Stream went online/offline
    private void stateChanged(MediaServerEvent event) {
        boolean alive = getMediaServerEventAlive(event);
        String aliveStr = alive ? "online" : "offline";
        //log.trace("State changed '{}': {}", event.getStreamKey(), aliveStr);
        StreamKey key = event.getStreamKey();
        streamManager.changeAlive(key, alive);
    }



    // ============================================================================


    
    private boolean  getMediaServerEventAlive(MediaServerEvent event) {
        boolean result;

        if (event.getEventType() == MediaServerEventType.SOURCE_READY) {
            result = true;
        }
        else if (event.getEventType() == MediaServerEventType.SOURCE_LOST) {
            result = false;
        }
        else {
            throw new IllegalArgumentException("Illegal event.type: " + event.getEventType());
        }
        return result;
    }
}
