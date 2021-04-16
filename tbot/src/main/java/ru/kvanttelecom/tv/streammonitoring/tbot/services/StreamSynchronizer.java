package ru.kvanttelecom.tv.streammonitoring.tbot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.tbot.services.amqp.StreamRpcClient;
import ru.kvanttelecom.tv.streammonitoring.tbot.configurations.properties.TBotProperties;
import ru.kvanttelecom.tv.streammonitoring.tbot.services.telegram.Telebot;

/**
 * Synchronize streams with monitor
 */
@Service
@Slf4j
public class StreamSynchronizer {

    @Autowired
    private StreamRpcClient streamRpcClient;

    @Autowired
    private Telebot telebot;

    @Autowired
    TBotProperties props;

//    /**
//     * Reload all streams from monitor
//     */
//    public void syncAll() {
//        log.trace("StreamSynchronizer: Reloading all streams");
//
//        Map<Long, Stream> updates = streamRpcClient.findAll().stream()
//            .collect(Collectors.toMap(Stream::getId, Function.identity()));
//
//        // prevent client to see empty streams Map
//        // ---------------------------------------------------------------
//        // 1. Remove from streams that not exists in update
//        streams.removeIf(s -> !updates.containsKey(s.getKey()));
//        // 2. Put all from update to streams - replace existing
//        streams.putAll(updates);
//        // ---------------------------------------------------------------
//
//        log.trace("Updated: {} streams", updates.size());
//    }


//    /**
//     * Parse update event from monitor
//     */
//    public void syncFromEvent(List<StreamEventDto> update) {
//
//        log.trace("StreamSynchronizer: syncFromEvent");
//
//        // get StreamKey from event update list
//        //Set<StreamKey> streamKeys = update.stream().map(StreamEventDto::getStreamKey).collect(Collectors.toSet());
//
//        // go to monitor and get full info about updated streams
//        //Map<StreamKey, Stream> alteredStreams = streamRpcClient.findByKeys(streamKeys);
//
//        // updating all local streams (if alteredStreams have any)
//        streams.putAll(null); // alteredStreams
//
//        // Sending message to telegram
//        // Doing it before deleting streams or will have no info about deleted stream
//        sendToTelegram(update, null);// alteredStreams
//
//        // DELETING STREAMS -----------------------------------------------------
//
//        // Теперь сравниваем events и alteredStreams
//        // Если в events было сообщение об удаленных стримах, то
//        // в alteredStreams этих стримов уже не будет(перед отправкой event monitor их уже удалил).
//        // вычисляем разницу - получаем стримы для удаления из streams
//
//        //Set<String> fromEvents = events.stream().map(StreamEventDto::getName).collect(Collectors.toSet());
//        //Set<String> fromAltered = new HashSet<>(alteredStreams.keySet());
//
//        // теперь в fromEvents содержатся стримы, которые были удалены на monitor
//        //fromEvents.removeAll(fromAltered);
//
//        // удаляем локально стримы из fromEvents
////        if(fromEvents.size() > 0) {
////            log.debug("DELETED STREAMS: {}", fromEvents);
////            streams.removeAll(fromEvents);
////        }
//    }


    // =============================================================================================


//    private void sendToTelegram(List<StreamEventDto> events) {
//
//        List<String> lines = new ArrayList<>();
//        for (StreamEventDto event : events) {
//
//            // Filter events -----------------------------------------------------------------
//            // 1. remove INIT streams
//            // 2. remove flapping streams
//            Predicate<StreamEventDto> noInitEvent = e -> !e.getEventSet().contains(StreamEventType.INIT);
//
//            Predicate<StreamEventDto> noFlapEvent = e ->
//                !e.getEventSet().contains(StreamEventType.START_FLAPPING) &&
//                !e.getEventSet().contains(StreamEventType.STOP_FLAPPING);
//
//            Predicate<Stream> noFlapStream = c -> !c.isFlapping();
//
//            if(noInitEvent.test(event) &&
//               noFlapEvent.test(event) &&
//                noFlapStream.test(stream)) {
//
//                lines.add(stream.getTitle() + " " + event.getEventSet().toString() + "\n");
//            }
//        }
//
//        lines.sort(String::compareTo);
//        StringBuilder sb = new StringBuilder();
//        lines.forEach(sb::append);
//        String message = sb.toString();
//
//        // sending to telegram bot group - skip on error
//        if (notBlank(message)) {
//            try {
//                telebot.sendMessage(props.getBotGroup(), message);
//            } catch (Exception skip) {
//                log.error("Sending telebot error ", skip);
//            }
//        }
//    }
}
