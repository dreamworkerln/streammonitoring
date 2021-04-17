package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.mappers.stream.StreamMapper;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamEventDto;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.mappers.streamstate.StreamStateMapper;
import ru.kvanttelecom.tv.streammonitoring.core.services.caching.StreamMultiService;
import ru.kvanttelecom.tv.streammonitoring.core.services.caching.StreamStateMultiService;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.downloader.StreamDownloader;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static ru.dreamworkerln.spring.utils.common.StringUtils.formatMsg;


/**
 * Stream manager -
 * Import Streams from different sources (Watcher / Mediaserver)
 * Remove streams
 * <br>Call StreamStatusService on import/delete operations
 *
 */
@Transactional
@Slf4j
public class StreamManager {

    private final StreamMapper streamMapper;
    private final StreamStateMapper streamStateMapper;
    private final StreamMultiService streamMultiService;
    private final MonitorProperties props;
    private final StreamDownloader streamDownloader;
    private final StreamStateMultiService streamStateMultiService;

    public StreamManager(StreamMapper streamMapper, StreamStateMapper streamStateMapper, StreamMultiService streamMultiService,
                         StreamStateMultiService streamStateMultiService,
                         StreamDownloader streamDownloader,
                         MonitorProperties props) {

        this.streamMapper = streamMapper;
        this.streamStateMapper = streamStateMapper;
        this.streamMultiService = streamMultiService;
        this.streamStateMultiService = streamStateMultiService;
        this.streamDownloader = streamDownloader;
        this.props = props;
    }







    /**
     * Synchronize all local streams with remote source
     */
    public void scanAll() {
        Map<StreamKey,StreamEventDto> result = new HashMap<>();

        // 1. Скачать все streamDto
        // 2. Создать/обновить все Stream
        // 3. Создать/обновить все StreamStatus
        // 4. Отметить все существующие только локально StreamStatus как disabled
        // 5. Публикация событий


        // флаг, что в систему не было занесено ни одного стрима
        boolean firstRun = streamMultiService.size() == 0;

        // 1.
        List<StreamDto> dtoList = streamDownloader.getAll();

        // 2.
        List<Stream> updatesStream = streamMapper.toEntityList(dtoList);
        Map<StreamKey,StreamEventDto> u1 = updateStreams(updatesStream);
        result.putAll(u1);

        // 3.
        List<StreamState> updateStates = streamStateMapper.toEntityList(dtoList);
        Map<StreamKey,StreamEventDto> u2 = updateStreamStates(updateStates);
        result.putAll(u2);

        // 4.
        Map<StreamKey,StreamEventDto> u3 = checkExpiredStreamStats(updateStates);
        result.putAll(u3);

        // 5.
        if(!firstRun) {
            messageSink(new ArrayList<>(result.values()));
        }
    }


    public void start(StreamKey key) {
        Map<StreamKey,StreamEventDto> result = new HashMap<>();

        // 1. Создать/обновить StreamStatus - указать enable, alive взять текущий, если он есть
        // 2. Скачать Stream
        // 3. Создать/обновить Stream
        // 4. Публикация событий


        // 1.
        StreamState updateState = new StreamState(key, true, false);
        streamStateMultiService.findByStreamKey(key).ifPresent(local -> updateState.setAlive(local.isAlive()));
        updateState.setEnabled(true);
        Map<StreamKey,StreamEventDto> u1 = updateStreamStates(Collections.singletonList(updateState));
        result.putAll(u1);

        // 2.
        Optional<StreamDto> oDto = streamDownloader.getOne(key);

        // 3.
        oDto.ifPresent(dto -> {
            Stream update = streamMapper.toEntity(dto);
            Map<StreamKey,StreamEventDto> u2 = updateStreams(Collections.singletonList(update));
            result.putAll(u2);
        });
        oDto.ifPresentOrElse(unused ->{}, () -> log.warn("Downloading stream '" + key + "' - stream not found"));

        // 4.
        messageSink(new ArrayList<>(result.values()));
    }


    /**
     * Stop stream
     * @param key streamKey
     */
    public void stop(StreamKey key) {

        // 1. Обновить StreamStatus - указать что enabled = false, alive = false

        Map<StreamKey,StreamEventDto> eventMap = new HashMap<>();

        Optional<StreamState> oState = streamStateMultiService.findByStreamKey(key);
        oState.ifPresentOrElse(unused ->{}, () -> log.warn("Stopping stream '" + key + "' - stream not found"));

        oState.ifPresent(local -> {
            StreamState update = new StreamState(key, false, false);
            Set<StreamEventType> events = streamStateMultiService.update(update);
            aggregateEvents(key, events, eventMap);
            messageSink(new ArrayList<>(eventMap.values()));
        });
    }


    /**
     * Update aliveness of Stream
     */
    public void changeAlive(StreamKey key, boolean alive) {

        // 1. Обновить StreamStatus enabled взять что есть, alive взять из обновления

        Map<StreamKey,StreamEventDto> eventMap = new HashMap<>();

        Optional<StreamState> oState = streamStateMultiService.findByStreamKey(key);
        oState.ifPresent(local -> {
            StreamState update = new StreamState(key, local.isEnabled(), alive);
            Set<StreamEventType> events = streamStateMultiService.update(update);
            aggregateEvents(key, events, eventMap);
            messageSink(new ArrayList<>(eventMap.values()));
        });
        oState.ifPresentOrElse(unused ->{}, () -> log.warn("Changing stream alive '" + key + "' - stream not found"));
    }


    // ===========================================================================================

    private synchronized Map<StreamKey,StreamEventDto> updateStreams(List<Stream> updates) {
        Map<StreamKey,StreamEventDto> result = new HashMap<>();

        // Checking updates streams for duplicate names(strict)/titles/addresses
        checkStreamNameDuplicates(updates);

        List<Stream> newStreams = new ArrayList<>();
        List<Stream> modifiedStreams = new ArrayList<>();

        updates.forEach( s -> {

            StreamKey key = s.getStreamKey();
            // new stream
            if(s.getId() == null) {
                newStreams.add(s);
                aggregateEvent(key, StreamEventType.CREATED, result);
            }
            // existing stream
            else {
                Stream l = streamMultiService.findByStreamKey(key)
                    .orElseThrow(() -> new IllegalArgumentException(formatMsg("Stream '{}' not found", key)));
                // stream has been changed
                if (!s.equals(l)) {
                    modifiedStreams.add(s);
                    aggregateEvent(key, StreamEventType.UPDATED, result);
                }
            }
        });

        // save all new streams
        streamMultiService.saveAll(newStreams);

        // update existing streams
        streamMultiService.saveAll(modifiedStreams);

        // do nothing with deleted streams (that exists only locally)

        // -----------------------------------------------------------------------------------
        // ToDo: периодически пылесосить сервисы StreamMultiService, StreamStateMultiService
        //       на предмет устаревших стримов, т.к. удаление стримов не происходит
        // -----------------------------------------------------------------------------------

        return result;
    }




    private synchronized Map<StreamKey,StreamEventDto> updateStreamStates(List<StreamState> updates) {
        Map<StreamKey,StreamEventDto> result = new HashMap<>();

        // new states
        List<StreamState> newStates = new ArrayList<>();

        updates.forEach(st -> {

            StreamKey key = st.getStreamKey();
            // new state
            if(st.getId() == null) {
                newStates.add(st);
            }
            else {
                Set<StreamEventType> events = streamStateMultiService.update(st);
                aggregateEvents(st.getStreamKey(), events, result);
            }
        });
        streamStateMultiService.saveAll(newStates);

        return result;
    }



    // Mediaserver (old version) on stream disabling do not set stream.enabled=false in API
    // but simply remove this whole stream from API response
    private Map<StreamKey,StreamEventDto> checkExpiredStreamStats(List<StreamState> updates) {
        Map<StreamKey,StreamEventDto> result = new HashMap<>();

        // Put to Map
        Map<StreamKey,StreamState> updateStateMap = Maps.uniqueIndex(updates, StreamState::getStreamKey);

        // finally find all states that exists only locally and mark them as disabled
        streamStateMultiService.findAll().stream().filter(st -> !updateStateMap.containsKey(st.getStreamKey()))
            .forEach(st -> {
                StreamState update = new StreamState(st.getStreamKey(), false, st.isAlive());
                Set<StreamEventType> events = streamStateMultiService.update(update);
                aggregateEvents(st.getStreamKey(), events, result);
            });

        return result;
    }










    /*
    private synchronized void addInternal(List<StreamDto> dtoList, boolean isFullUpdate) {

        // events, generating in update process
        Map<StreamKey,StreamEventDto> eventMap = new HashMap<>();

        // Convert dto to Stream
        List<Stream> updates = streamMapper.toEntityList(dtoList);

        // Checking updates streams for duplicate names(strict)/titles/addresses
        checkStreamNameDuplicates(updates);

        // флаг, что в систему не было занесено ни одного стрима
        boolean firstRun = streamMultiService.size() == 0;

        // Список событий по изменению стримов (как Stream так и StreamState)
        //Map<StreamKey,Set<StreamEventType>> stateEvents = new HashMap<>();

        // -----------------------------------------------------------------------------------
        // Streams SYNC ----------------------------------------------------------------------
        // -----------------------------------------------------------------------------------

        List<Stream> newStreams = new ArrayList<>();
        List<Stream> modifiedStreams = new ArrayList<>();

        updates.forEach( s -> {

            StreamKey key = s.getStreamKey();
            // new stream
            if(s.getId() == null) {
                newStreams.add(s);
                aggregateEvent(key, StreamEventType.CREATED, eventMap);
            }
            // existing stream
            else {
                Stream l = streamMultiService.findByStreamKey(key)
                    .orElseThrow(() -> new IllegalArgumentException(formatMsg("Stream '{}' not found", key)));
                // stream has been changed
                if (!s.equals(l)) {
                    modifiedStreams.add(s);
                    aggregateEvent(key, StreamEventType.UPDATED, eventMap);
                }
            }
        });

        // save all new streams
        streamMultiService.saveAll(newStreams);

        // update existing streams
        streamMultiService.saveAll(modifiedStreams);

        // do nothing with deleted streams (present only locally)

        // -----------------------------------------------------------------------------------
        // ToDo: периодически пылесосить сервисы StreamMultiService, StreamStateMultiService
        //       на предмет устаревших стримов, т.к. удаление стримов не происходит
        // -----------------------------------------------------------------------------------





        // -----------------------------------------------------------------------------------
        // StreamState SYNC ------------------------------------------------------------------
        // -----------------------------------------------------------------------------------


        // Convert dto to StreamState
        List<StreamState> updateStates = streamStateMapper.toEntityList(dtoList);

        // new states
        List<StreamState> newStates = new ArrayList<>();

        updateStates.forEach(st -> {

            StreamKey key = st.getStreamKey();
            // new state
            if(st.getId() == null) {
                newStates.add(st);
                // no event generation - событие о добавлении стрима уже было создано выше, в Streams SYNC
            }
            else {

                // read current alive status
                if(!isFullUpdate) {
                    StreamState l = streamStateMultiService.findByStreamKey(key)
                        .orElseThrow(() -> new IllegalArgumentException(formatMsg("StreamState '{}' not found", key)));
                    st.setAlive(l.isAlive());
                }
                Set<StreamEventType> events = streamStateMultiService.update(st);
                aggregateEvents(st.getStreamKey(), events, eventMap);
            }
        });
        streamStateMultiService.saveAll(newStates);


        if(isFullUpdate) {

            // Put to Map
            Map<StreamKey,StreamState> updateStateMap = Maps.uniqueIndex(updateStates, StreamState::getStreamKey);

            // finally find all states that exists only locally and mark them as disabled
            streamStateMultiService.findAll().stream().filter(st -> !updateStateMap.containsKey(st.getStreamKey()))
                .forEach(st -> {
                    StreamState update = new StreamState(st.getStreamKey(), false, st.isAlive());
                    Set<StreamEventType> events = streamStateMultiService.update(update);
                    aggregateEvents(st.getStreamKey(), events, eventMap);
                });
            // Mediaserver (old version) on stream disabling do not set stream.enabled=false in API
            // but simply remove this whole stream from API response
        }


        // -----------------------------------------------------------------------------------
        // Publishing stream updates
        // -----------------------------------------------------------------------------------

        // Do not generate events if application is not initialization (have no streams stored)
        // So do not generate events when streams was first time loaded into system (on startup)
        if(!firstRun) {
            messageSink(new ArrayList<>(eventMap.values()));
        }
        // -------------------------------------------------------------------------------------

    }
    */


    /**
     * Receive here all calculated updates about streams
     */
    private void messageSink(List<StreamEventDto> events) {

        if(events.size() == 0) {
            return;
        }

//        // DEBUGGING ------------------------------------------------
//        StreamKey oprKey = new StreamKey("t8", "camass-275fe84429");
//        Optional<Stream> oS = streamMultiService.findByStreamKey(oprKey);
//        if(oS.isPresent()) {
//            Stream s = oS.get();
//            log.info("\n\nCAM : {}\n\n", streamStateMultiService.get(oprKey));
//        }
//        else {
//            log.info("\n\nCAM not found\n\n");
//        }
//        // ---------------------------------------------------------------


        log.debug("{}", events);
    }



    private void aggregateEvent(StreamKey key, StreamEventType event, Map<StreamKey,StreamEventDto> map) {

        // create new event
        if (!map.containsKey(key)) {
            map.put(key, new StreamEventDto(key, Set.of(event)));
        }
        // update to existing event
        else {
            map.get(key).getEventSet().add(event);
        }
    }


    private void aggregateEvents(StreamKey key, Set<StreamEventType> events, Map<StreamKey,StreamEventDto> map) {

        if(events.size() == 0) {
            return;
        }

        // create new event
        if (!map.containsKey(key)) {
            map.put(key, new StreamEventDto(key, events));
        }
        // update to existing event
        else {
            map.get(key).getEventSet().addAll(events);
        }
    }

// =================================================================================================================


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
    protected void checkStreamNameDuplicates(/*Map<StreamKey,Stream> streams*/ List<Stream> streams) {

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

            for (Stream stream : streams) {
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
