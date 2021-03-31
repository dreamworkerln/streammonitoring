package ru.kvanttelecom.tv.streammonitoring.monitor.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.events.mediaserver.MediaServerEvent;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.MediaServerEventParser;

import java.util.List;

@RestController
@Slf4j
public class MediaServerEventsReceiver {

    @Autowired
    private MediaServerEventParser parser;

    //@Autowired
    //MediaServiceEventAppender eventAppender;

    /**
     * receive updates from flussonic media server
     * <br>Consume array ov events
     * @param json
     */
    @PostMapping("/mediaserver_events")
    public void  processRequest(@RequestBody String json) {

        try {
            log.trace("Received: {}", json);
            List<MediaServerEvent> events = parser.parseEvent(json);
            //eventAppender.append(events);
        }
        catch(Exception skip) {
            log.error("MediaServerEventsReceiver error:", skip);
        }





    }
}
