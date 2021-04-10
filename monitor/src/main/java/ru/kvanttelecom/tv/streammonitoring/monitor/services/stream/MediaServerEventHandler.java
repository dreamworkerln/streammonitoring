package ru.kvanttelecom.tv.streammonitoring.monitor.services.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.streammonitoring.core.services.stream.StreamService;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.enums.MediaServerEventType;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.events.mediaserver.MediaServerEvent;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;


@Component
@Slf4j
public class MediaServerEventHandler {

    private Map<MediaServerEventType, Consumer<MediaServerEvent>> handlers = new HashMap<>();

    @Autowired
    StreamStateService stateService;

    @Autowired
    StreamService streamService;


    @PostConstruct
    private void init() {

        handlers.put(MediaServerEventType.UNKNOWN, this::unknownHandler);
        handlers.put(MediaServerEventType.SOURCE_READY, this::unknownHandler);
    }



    public void applyEvents(List<MediaServerEvent> events) {

        // Don't receive events if StreamService not initialized
        if(!streamService.isInitialized()) {
            return;
        }

        String streamKey;

        for (MediaServerEvent event : events) {
            streamKey = getStreamKey(event);



        }
    }



    // ---------------------------------------------------------------------------------

    private void unknownHandler(MediaServerEvent event) {
        log.warn("Unknown MediaServerEvent: {}", event);
    }

    private void lostHandler(MediaServerEvent event) {
        log.trace("MediaServerEvent: {}", event);
        stateService.process();
    }

    private Boolean readyHandler(MediaServerEvent event) {
        log.trace("MediaServerEvent: {}", event);

        return false;
    }

    // -----------------------------------------------------------------




    private static String getStreamKey(String hostname, String name) {
        Assert.notNull(hostname, "hostname == null");
        Assert.notNull(name, "name == null");
        return hostname + "." + name;
    }

    private static String getStreamKey(MediaServerEvent event) {
        Assert.notNull(event.getHostname(), "hostname == null");
        Assert.notNull(event.getStreamName(), "name == null");
        return event.getHostname() + "." + event.getStreamName();
    }





}
