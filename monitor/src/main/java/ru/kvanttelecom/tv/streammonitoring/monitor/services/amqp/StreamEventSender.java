package ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.AmqpId;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamEventDto;

import java.util.List;

@Service
@Slf4j
public class StreamEventSender {

    @Autowired
    private RabbitTemplate template;

    public void send(List<StreamEventDto> events) {

        String exchanger = AmqpId.exchanger.stream.events.update;
        String routing = AmqpId.binding.stream.events.update;

        log.trace("SENDING STREAM EVENTS: {}", events);
        template.convertAndSend(exchanger, routing, events);
        //log.trace("STREAM EVENTS SEND");
    }

}
