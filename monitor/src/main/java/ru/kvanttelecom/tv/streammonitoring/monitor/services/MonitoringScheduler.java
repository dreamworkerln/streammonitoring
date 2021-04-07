package ru.kvanttelecom.tv.streammonitoring.monitor.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services.server.ServerService;
import ru.kvanttelecom.tv.streammonitoring.core.services.stream.StreamService;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.events.mediaserver.MediaServerEvent;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp.StreamEventSender;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.WatcherGrabber;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;


/**
 * Main monitoring process, has been scheduled in interval
 */
@Service
@Slf4j
public class MonitoringScheduler {

    private boolean checkStreamUniq;

    @Autowired
    private WatcherGrabber watcherGrabber;

    @Autowired
    private StreamService streamService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private StreamEventSender streamEventSender;

    @Autowired
    private MonitorProperties props;


    @PostConstruct
    private void postConstruct() {
        checkStreamUniq = props.isCheckStreamUniq();
    }



    /**
     * Update cameras info from Watcher
     */
    //
    @Scheduled(fixedDelayString = "#{monitorProperties.getRefreshIntervalSec() * 1000}",
        initialDelayString = "#{monitorProperties.getRefreshIntervalSec() * 1000*36000}")
    private void updateStreams() {

        try {

            log.trace("MONITOR - UPDATE STREAMS ==============================================");

//            // Events from all servers
//            List<StreamEventDto> events = new ArrayList<>();


            // 1. Load Streams from watcher ----------------------------------------

            // Get all streams in system
            Map<String,Stream> streams = streamService.findAll().stream()
                .collect(Collectors.toMap(Stream::getName, Function.identity()));

            // Get servers
            List<Server> servers = serverService.findAll();

            // Get new streams
            List<Stream> watcherStreams = watcherGrabber.getStreamList();

            // Checking for duplicate names(strict)/titles/addresses
            checkStreamNameDuplicates(watcherStreams);

            List<Stream> toDelete = new ArrayList<>();
            List<Stream> toUpdate = new ArrayList<>();

            for (Stream ws : watcherStreams) {
            }










            // StreamUpdate name index - checking stream uniqueness (on all servers)
            // Map<String, MediaServerEvent> nameIndex = new HashMap<>();

            // обходим по всем серверам
            for (Server server : servers) {

                String serverName = null;
                try {
                    serverName = server.getDomainName();

                    //log.trace("Scanning server: {}", serverName);

                    // get updates from specified flussonic media server
                    List<MediaServerEvent> update = new ArrayList<>(); // apiClient.getStreamsUpdate(serverName);


                    //checkDuplicate(update, nameIndex);

/*                    // FixMe - удалить try catch
                    try {
                        checkDuplicate(update, nameIndex);
                    }
                    catch (Exception skip) {
                        log.error("Found duplicate streams on server: {}", serverName, skip);
                    }
*/


                    // calculate update events for selected server
                    //List<StreamEventDto> serverEvents = streamService. .applyUpdate(serverName, update);

                    //events.addAll(serverEvents);
                }
                // have error - continue to next server
                catch (Exception skip) {
                    log.error("Getting data from server {} error:", serverName, skip);
                }
            }

            // sending to bot ---------------------------------------
//            if (events.size() > 0) {
//                // sending events to message aggregator(bot)
//                try {
//                    streamEventSender.send(events);
//                }
//                // skip on error
//                catch (Exception skip) {
//                    log.error("Sending to aggregator(bot) error:", skip);
//                }
//            }
//            else {
//                //log.trace("Nothing to send");
//            }

        }
        // Unexpected error - shutdown program
        catch (Exception fatal) {
            log.error("Monitor strange error, shutdown: ", fatal);
            System.exit(-1);
        }
    }

    /**
     * Validating streams for duplicate name/title
     */
    private void checkStreamNameDuplicates(List<Stream> streams) {

        List<EntityIndex<String,Stream>> checkers = new ArrayList<>();

        checkers.add(new EntityIndex<>(
            Stream::getName,
            (s1, s2) -> {
                throw new IllegalArgumentException(streamDuplicateFormatter("DUPLICATE STREAM NAME", s1,s2));
            }));

        checkers.add(new EntityIndex<>(
            Stream::getTitle,
            (s1, s2) -> {
                log.info("POSSIBLY DUPLICATE CAMERA: " + streamDuplicateFormatter("DUPLICATE STREAM TITLE", s1,s2));
            }));

//        checkers.add(new EntityIndex<>(
//            s -> s.getAddress().getPostAddress(),
//            (s1, s2) -> {
//                log.info("POSSIBLY DUPLICATE CAMERA: " + streamDuplicateFormatter("DUPLICATE STREAM ADDRESS", s1,s2));
//            }));

        for (Stream stream : streams) {

            for (EntityIndex<String, Stream> checker : checkers) {
                checker.check(stream);
            }
        }
    }

    /*    *//**
     * Check duplicate streams (by name) on different flussonic media servers
     * <b>If duplicate stream found on server then all streams update from this server will be rejected
     * @param update update from specific server
     * @param nameIndex streams name index of all streams on all servers
     *//*
    private void checkDuplicate(List<MediaServerEvent> update, Map<String, MediaServerEvent> nameIndex) {
        // check for duplicates

        if(!checkStreamUniq) {
            return;
        }

        List<StreamUpdateDuplicate> duplicates = new ArrayList<>();

        update.forEach(e -> {
            MediaServerEvent exists = nameIndex.get(e.getStreamName());
            if(nameIndex.containsKey(e.getStreamName())) {
                duplicates.add(new StreamUpdateDuplicate(exists, e));
            }
            else {
                nameIndex.put(e.getStreamName(), e);
            }
        });

        if(duplicates.size() > 0) {
            StringBuilder sb = new StringBuilder("Found duplicate streams:\n");

            String prefix = "";
            for (StreamUpdateDuplicate d : duplicates) {
                sb.append(prefix);
                prefix = "\n";
                sb.append(d);

*//*                // FixMe  - удалить строку, поиск сканированием всех строк
                // Добавил, чтобы удалялись дубликаты стримов, иначе весь update будет отброшен
                update.removeIf(u->u.getName().equals(d.exists.getName()));
*//*
            }
            throw new IllegalArgumentException(sb.toString());
        }
    }*/


    private static class EntityIndex<K,V> {
        private final Map<K, V> index = new HashMap<>();

        // index field extractor
        private final Function<V,K> extractor;
        private final BiConsumer<V, V> formatter;

        public EntityIndex(Function<V, K> extractor, BiConsumer<V, V> formatter) {
            this.extractor = extractor;
            this.formatter = formatter;
        }

        public void check(V value) {

            K key = extractor.apply(value);
            V exists = index.get(key);
            if(exists != null) {
                formatter.accept(exists, value);
            }
            else {
                index.put(key, value);
            }
        }
    }

//
//    @RequiredArgsConstructor
//    private static class CheckResult {
//        public final boolean fatal;
//        public final String result;
//    }
    private static String streamDuplicateFormatter(String message, Stream exists, Stream duplicate) {
        return message + " " + ": [" +
            "exists={name='" + exists.getName() + "', title='" + exists.getTitle() +
            "', server=" + exists.getServer().getHostname() + "}, " +
            "event={name='" + duplicate.getName() + "', title='" + duplicate.getTitle() +
            "', server=" + duplicate.getServer().getHostname() + "}]";
    }


}
