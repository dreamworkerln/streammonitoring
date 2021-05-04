package ru.kvanttelecom.tv.streammonitoring.tbot.services.amqp;

import com.google.common.collect.Maps;
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
import java.util.Map;
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

        try {
            log.debug("STREAM EVENT: {}", update);

            // get stream keys from update
            List<StreamKey> keys = update.stream().map(StreamEventDto::getKey).collect(Collectors.toList());

            // get Streams for corresponding keys
            List<StreamDto> streamList = rpcClient.findStreamByKeyList(keys);
            // build Map
            Map<StreamKey, StreamDto> streams = Maps.uniqueIndex(streamList, StreamDto::getStreamKey);


            StringBuilder sb = new StringBuilder();
            // iterate thru update events
            for (StreamEventDto event : update) {
                StreamDto stream = streams.get(event.getKey());
                if(stream == null) {
                    //return;
                    throw new RuntimeException("Stream '" + event.getKey() + "' not found on monitor");
                }
                String name = notBlank(stream.getTitle()) ? stream.getTitle() : stream.getName();
                sb.append(name).append(": ").append(event.getEventSet()).append("\n");
            }
            bot.sendMessage(props.getBotGroup(), sb.toString());
        }
        catch(Exception skip) {
            log.error("STREAM EVENT ERROR:", skip);
        }
    }

}

//@RabbitListener(queues = "#{queueStreamEvent.getName()}")
