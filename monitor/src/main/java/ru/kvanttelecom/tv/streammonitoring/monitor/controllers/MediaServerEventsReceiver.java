package ru.kvanttelecom.tv.streammonitoring.monitor.controllers;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.events.mediaserver.MediaServerEvent;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.parser.MediaServerEventParser;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.eventhandlers.MediaServerEventHandler;

import java.util.List;

@RestController
@Slf4j
public class MediaServerEventsReceiver {

    Marker marker = MarkerFactory.getMarker("MEDIASERVER_EVENT");

    @Autowired
    private MediaServerEventParser parser;

    @Autowired
    private MediaServerEventHandler eventHandler;


    /**
     * Receive updates from flussonic media server
     * <br>Consume list of events
     */
    @PostMapping("/mediaserver_events")
    public void processRequest(@RequestBody String json) {
        try {
            log.trace(marker, "MEDIASERVER EVENT: {}", json);
            List<MediaServerEvent> events = parser.getArray(json);
            eventHandler.applyEvents(events);
        }
        catch(Exception skip) {
            log.error("MediaServerEventsReceiver error:", skip);
        }
    }
}
