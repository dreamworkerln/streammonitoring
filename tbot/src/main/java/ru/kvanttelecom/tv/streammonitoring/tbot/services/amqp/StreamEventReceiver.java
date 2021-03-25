package ru.kvanttelecom.tv.streammonitoring.tbot.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.tbot.services.StreamSynchronizer;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.StreamEventDto;

import java.util.List;

@Service
@Slf4j
public class StreamEventReceiver {

    @Autowired
    StreamSynchronizer synchronizer;

    @RabbitListener(queues = "#{queueStreamEvent.getName()}")
    private void receive(List<StreamEventDto> events) {

        try {
            log.trace("STREAM EVENT: {}", events);
            synchronizer.syncFromEvent(events);

        }
        catch(Exception rethrow) {
            log.error("Synchronization fromEvent error: ", rethrow);
            throw rethrow;
        }
    }

}
