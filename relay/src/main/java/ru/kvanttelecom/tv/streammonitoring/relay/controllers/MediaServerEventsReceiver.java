package ru.kvanttelecom.tv.streammonitoring.relay.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.kvanttelecom.tv.streammonitoring.relay.services.EventSender;

@RestController
@Slf4j
public class MediaServerEventsReceiver {

    @Autowired
    private EventSender eventSender;

    @PostMapping
    public void  processRequest(@RequestBody String json) {
        log.trace("Received: {}", json);
        eventSender.send(json);
    }
}
