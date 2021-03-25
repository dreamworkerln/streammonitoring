package ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.StreamEventDto;

import java.util.List;

@Service
@Slf4j
public class StreamEventSender {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange exchangeStreamUpdate;

    @Autowired
    private Binding bindingStreamUpdate;


    public void send(List<StreamEventDto> events) {

        String exchanger = exchangeStreamUpdate.getName();
        String routing = bindingStreamUpdate.getRoutingKey();

        log.trace("SENDING STREAM EVENTS: {}", events);
        template.convertAndSend(exchanger, routing, events);
        //log.trace("STREAM EVENTS SEND");
    }

}
