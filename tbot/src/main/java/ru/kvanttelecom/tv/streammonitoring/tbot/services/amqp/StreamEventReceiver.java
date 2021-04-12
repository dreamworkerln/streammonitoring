package ru.kvanttelecom.tv.streammonitoring.tbot.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.AmqpId;
import ru.kvanttelecom.tv.streammonitoring.tbot.services.StreamSynchronizer;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamEventDto;

import java.util.List;

@Service
@Slf4j
public class StreamEventReceiver {

    @Autowired
    StreamSynchronizer synchronizer;


    
//    @RabbitListener(queues = AmqpId.queue.stream.events.update)
//    private void receive(List<StreamEventDto> update) {
//
//        try {
//            log.trace("STREAM EVENT: {}", update);
//            synchronizer.syncFromEvent(update);
//
//        }
//        catch(Exception rethrow) {
//            log.error("Synchronization fromEvent error: ", rethrow);
//            throw rethrow;
//        }
//    }

}

//@RabbitListener(queues = "#{queueStreamEvent.getName()}")
