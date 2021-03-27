package ru.kvanttelecom.tv.streammonitoring.monitor.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services.StreamService;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.events.mediaserver.MediaServerEvent;

import java.util.List;
import java.util.Optional;


/**
 * Update Stream according to incoming MediaServerEvent
 */
@Service
@Slf4j
public class MediaServiceEventAppender {

    private static final int STREAM_MAX_LEVEL = 10;
    private static final int STREAM_THRESHOLD_LEVEL = (int)(STREAM_MAX_LEVEL * 0.7);
    private static final Double STREAM_FLAPPING_MIN_RATE = 1./600; // 1 раз в 10 мин

    @Autowired
    private StreamService streamService;

    public void append(List<MediaServerEvent> list) {

        for (MediaServerEvent event : list) {

            // 1. load Stream
            Optional<Stream> stream = streamService.findByKey(event.getStreamKey());
        }





    }


}
