package ru.kvanttelecom.tv.streammonitoring.tbot.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.tbot.services.StreamSynchronizer;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.StreamEvent;

import java.util.List;

@Service
@Slf4j
public class CameraEventReceiver {

    @Autowired
    StreamSynchronizer synchronizer;

    @RabbitListener(queues = "#{cameraUpdateQueue.getName()}")
    private void receive(List<StreamEvent> events) {

        try {
            log.trace("CAMERA EVENT: {}", events);
            synchronizer.syncFromEvent(events);

        }
        catch(Exception rethrow) {
            log.error("Synchronization fromEvent error: ", rethrow);
            throw rethrow;
        }
    }

}
