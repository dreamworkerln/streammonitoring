package ru.kvanttelecom.tv.streammonitoring.tbot.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.AmqpId;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;
import ru.kvanttelecom.tv.streammonitoring.tbot.configurations.properties.TBotProperties;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamEventDto;
import ru.kvanttelecom.tv.streammonitoring.tbot.services.telegram.Telebot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.dreamworkerln.spring.utils.common.StringUtils.isBlank;
import static ru.dreamworkerln.spring.utils.common.StringUtils.notBlank;

@Service
@Slf4j
public class StreamEventReceiver {

    @Autowired
    StreamRpcClient rpcClient;

    @Autowired
    private Telebot bot;
    @Autowired
    private TBotProperties props;





    @RabbitListener(queues = AmqpId.queue.stream.events.update)
    private void receive(List<StreamEventDto> update) {

//        try {
//            log.trace("STREAM EVENT: {}", update);
//
//            StringBuilder sb = new StringBuilder();
//            for (StreamEventDto event : update) {
//                StreamDto stream = rpcClient.findStreamByKey(event.getKey());
//                String name = notBlank(stream.getTitle()) ? stream.getTitle() : stream.getName();
//                sb.append(name).append(": ").append(event.getEventSet()).append("\n");
//            }
//            bot.sendMessage(props.getBotGroup(), sb.toString());
//        }
//        catch(Exception rethrow) {
//            throw new RuntimeException("StreamRpcClient.receive error:", rethrow);
//        }
    }

}

//@RabbitListener(queues = "#{queueStreamEvent.getName()}")
