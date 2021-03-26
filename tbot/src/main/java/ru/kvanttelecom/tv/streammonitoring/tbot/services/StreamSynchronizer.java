package ru.kvanttelecom.tv.streammonitoring.tbot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.streammonitoring.tbot.configurations.properties.BotProperties;
import ru.kvanttelecom.tv.streammonitoring.tbot.services.amqp.StreamRpcClient;
import ru.kvanttelecom.tv.streammonitoring.tbot.services.telegram.Telebot;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.StreamEventDto;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;
import ru.kvanttelecom.tv.streammonitoring.utils.beans.StreamMap;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.dreamworkerln.spring.utils.common.StringUtils.notBlank;

/**
 * Synchronize streams with monitor
 */
@Service
@Slf4j
public class StreamSynchronizer {

    @Autowired
    private StreamMap streams;

    @Autowired
    private StreamRpcClient streamRpcClient;

    @Autowired
    private Telebot telebot;

    @Autowired
    BotProperties props;

    /**
     * Reload all streams from monitor
     */
    public void syncAll() {
        log.trace("StreamSynchronizer: Reloading all streams");

        Map<String, Stream> updates = streamRpcClient.findAll().stream()
            .collect(Collectors.toMap(Stream::getName, Function.identity()));

        // prevent client to see empty streams Map
        // ---------------------------------------------------------------
        // 1. Remove from streams that not exists in update
        streams.removeIf(entry -> !updates.containsKey(entry.getKey()));
        // 2. Put all from update to streams - replace existing
        streams.putAll(updates);
        // ---------------------------------------------------------------

        log.trace("Updated: {} streams", updates.size());
    }


    /**
     * Parse update event from monitor
     */
    public void syncFromEvent(List<StreamEventDto> events) {

        log.trace("StreamSynchronizer: syncFromEvent");

        // get streams names from events list
        List<String> names = events.stream().map(StreamEventDto::getName).collect(Collectors.toList());

        // go to monitor and get full info about updated streams
        Map<String, Stream> alteredStreams = streamRpcClient.findByName(names).stream()
            .collect(Collectors.toMap(Stream::getName, Function.identity()));

        // updating all local streams (if alteredStreams have any)
        streams.putAll(alteredStreams);

        // Sending message to telegram
        // Doing it before deleting streams or will have no info about deleted stream
        sendToTelegram(events, alteredStreams);

        // DELETING STREAMS -----------------------------------------------------

        // Теперь сравниваем events и alteredStreams
        // Если в events было сообщение об удаленных стримах, то
        // в alteredStreams этих стримов уже не будет(перед отправкой event monitor их уже удалил).
        // вычисляем разницу - получаем стримы для удаления из streams

        Set<String> fromEvents = events.stream().map(StreamEventDto::getName).collect(Collectors.toSet());
        Set<String> fromAltered = new HashSet<>(alteredStreams.keySet());

        // теперь в fromEvents содержатся стримы, которые были удалены на monitor
        fromEvents.removeAll(fromAltered);

        // удаляем локально стримы из fromEvents
        if(fromEvents.size() > 0) {
            log.debug("DELETED STREAMS: {}", fromEvents);
            streams.removeAll(fromEvents);
        }
    }


    // =============================================================================================


    private void sendToTelegram(List<StreamEventDto> events, Map<String, Stream> alteredStreams) {

        List<String> lines = new ArrayList<>();
        for (StreamEventDto event : events) {

            String name = event.getName();

            Stream stream = alteredStreams.get(name);

            // If stream has been deleted in monitor, borrow stream from local streams
            if(stream == null) {
                stream = streams.get(name);
            }

            Assert.notNull(stream, "Stream == null");

            // Filter events -----------------------------------------------------------------
            // 1. remove INIT streams
            // 2. remove flapping streams
            Predicate<StreamEventDto> noInitEvent = e -> !e.getEventSet().contains(StreamEventType.INIT);

            Predicate<StreamEventDto> noFlapEvent = e ->
                !e.getEventSet().contains(StreamEventType.START_FLAPPING) &&
                !e.getEventSet().contains(StreamEventType.STOP_FLAPPING);

            Predicate<Stream> noFlapStream = c -> !c.isFlapping();

            if(noInitEvent.test(event) &&
               noFlapEvent.test(event) &&
                noFlapStream.test(stream)) {

                lines.add(stream.getTitle() + " " + event.getEventSet().toString() + "\n");
            }
        }

        lines.sort(String::compareTo);
        StringBuilder sb = new StringBuilder();
        lines.forEach(sb::append);
        String message = sb.toString();

        // sending to telegram bot group - skip on error
        if (notBlank(message)) {
            try {
                telebot.sendMessage(props.getBotGroup(), message);
            } catch (Exception skip) {
                log.error("Sending telebot error ", skip);
            }
        }
    }
}
