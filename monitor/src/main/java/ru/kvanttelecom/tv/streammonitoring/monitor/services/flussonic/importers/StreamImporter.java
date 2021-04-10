package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services.stream.StreamService;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.stream.StreamStateService;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.downloader.StreamDownloader;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.dreamworkerln.spring.utils.common.SpringBeanUtilsEx.copyPropertiesExcludeNull;


/**
 * import Streams into system from different sources
 * <br>Currently from: Flussonic Watcher / Mediaserver
 */
@Transactional
@Slf4j
public class StreamImporter {

    private final StreamService streamService;
    private final MonitorProperties props;
    private final StreamDownloader streamDownloader;
    private final StreamStateService streamStateService;

    public StreamImporter(StreamService streamService,
                          StreamStateService streamStateService,
                          StreamDownloader streamDownloader,
                          MonitorProperties props) {

        this.streamService = streamService;
        this.streamStateService = streamStateService;
        this.streamDownloader = streamDownloader;
        this.props = props;
    }

    public synchronized void importAll() {

        boolean haveStreams;

        // Reload streams

        // Get new streams
        // <StreamKey,Stream>
        Map<String, Stream> importStreams = streamDownloader.getAll().stream()
            .collect(Collectors.toMap(Stream::getStreamKey, Function.identity()));

        // Checking for duplicate names(strict)/titles/addresses
        // only inside importStreams
        checkStreamNameDuplicates(importStreams);



        // Get all local streams (from DB)
        // <StreamKey,Stream>
        Map<String,Stream> streams = streamService.findAll().stream()
            .collect(Collectors.toMap(Stream::getStreamKey, Function.identity()));

        haveStreams = streams.size() > 0;

        // Calculate streams to create, delete, update
        List<Stream> toCreate = new ArrayList<>();
        List<Stream> toUpdate = new ArrayList<>();
        List<Stream> toDelete = new ArrayList<>();


        for (Map.Entry<String, Stream> entry : streams.entrySet()) {

            Stream stream = streams.get(entry.getKey());
            Stream watcherStream = importStreams.get(entry.getKey());

            // to delete
            if(watcherStream == null) {
                toDelete.add(entry.getValue());
            }
            // to update
            else {
                if(!watcherStream.equals(stream)) {
                    copyPropertiesExcludeNull(watcherStream, stream);
                    toUpdate.add(stream);
                }
            }
        }

        // to create
        for (Map.Entry<String, Stream> entry : importStreams.entrySet()) {
            Stream stream = streams.get(entry.getKey());
            Stream watcherStream = importStreams.get(entry.getKey());

            if(stream == null) {
                toCreate.add(watcherStream);
            }
        }

        haveStreams = haveStreams || toCreate.size() > 0;

        // guard
        streams = null;

        // Streams
        streamService.deleteAll(toDelete); // remove locally deprecated
        streamService.saveAll(toCreate);   // add new
        streamService.saveAll(toUpdate);   // update existing

        // Set StreamService initialization status
        if (haveStreams && !streamService.isInitialized()) {
            streamService.setInitialized(true);
        }

        // StreamStatus
        toDelete.forEach(streamStateService::remove); // remove locally deprecated
        toCreate.forEach(streamStateService::put);    // add new
        //////////////////////////////////////////////// no need to update -
        // cause all update events will send by flussonic notify events
        // and then will be received and registered in controller MediaServerEventsReceiver
    }


    public synchronized void importOne(String hostname, String name) {

        Optional<Stream> oStream = streamService.findByHostnameAndName(hostname, name);

        if(oStream.isPresent()) {
            return;
        }
        
        // download new stream
        oStream = streamDownloader.getOne(hostname, name);
        oStream.ifPresent(s -> {
            streamService.save(s);
            streamStateService.put(s);
        });
    }


    // ------------------------------------------------------------------------------


    private static class EntityIndex<K,V> {
        private final Map<K, V> index = new HashMap<>();

        // index field extractor
        private final Function<V,K> extractor;

        // constraint violation error formatter
        private final BiConsumer<V, V> formatter;

        public EntityIndex(Function<V, K> extractor, BiConsumer<V, V> formatter) {
            this.extractor = extractor;
            this.formatter = formatter;
        }

        public void check(V value) {
            K key = extractor.apply(value);

            if(key == null) {
                return;
            }
            
            V exists = index.get(key);
            if(exists != null) {
                formatter.accept(exists, value);
            }
            else {
                index.put(key, value);
            }
        }
    }


    /**
     * Validating streams for duplicate name/title
     */
    // WARN - not working with television channels
    protected void checkStreamNameDuplicates(Map<String,Stream> streams) {

        List<EntityIndex<String,Stream>> checkers = new ArrayList<>();

        if(props.isCheckStreamGlobalUniq()) {

            checkers.add(new EntityIndex<>(
                Stream::getName,
                (s1, s2) -> {
                    throw new IllegalArgumentException(streamDuplicateFormatter("DUPLICATE STREAM NAME", s1, s2));
                }));

            checkers.add(new EntityIndex<>(
                Stream::getTitle,
                (s1, s2) -> {
                    log.info("POSSIBLY DUPLICATE STREAM: " + streamDuplicateFormatter("DUPLICATE STREAM TITLE", s1, s2));
                }));

            for (Stream stream : streams.values()) {
                for (EntityIndex<String, Stream> checker : checkers) {
                    checker.check(stream);
                }
            }
        }
    }


    protected static String streamDuplicateFormatter(String message, Stream exists, Stream duplicate) {
        return message + " " + ": [" +
            "first={name='" + exists.getName() + "', title='" + exists.getTitle() +
            "', server=" + exists.getServer().getHostname() + "}, " +
            "second={name='" + duplicate.getName() + "', title='" + duplicate.getTitle() +
            "', server=" + duplicate.getServer().getHostname() + "}]";
    }
}


/*


//        checkers.add(new EntityIndex<>(
//            s -> s.getAddress().getPostAddress(),
//            (s1, s2) -> {
//                log.info("POSSIBLY DUPLICATE CAMERA: " + streamDuplicateFormatter("DUPLICATE STREAM ADDRESS", s1,s2));
//            }));




 */
