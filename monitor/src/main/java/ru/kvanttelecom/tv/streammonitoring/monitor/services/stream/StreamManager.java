package ru.kvanttelecom.tv.streammonitoring.monitor.services.stream;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
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
import ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp.stream.StreamEventSender;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.downloader.StreamDownloader;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final StreamEventSender streamEventSender;

    public StreamManager(StreamMapper streamMapper, StreamStateMapper streamStateMapper, StreamMultiService streamMultiService,
                         StreamStateMultiService streamStateMultiService,
                         StreamDownloader streamDownloader,
                         MonitorProperties props, StreamEventSender streamEventSender) {

        this.streamMapper = streamMapper;
        this.streamStateMapper = streamStateMapper;
        this.streamMultiService = streamMultiService;
        this.streamStateMultiService = streamStateMultiService;
        this.streamDownloader = streamDownloader;
        this.props = props;
        this.streamEventSender = streamEventSender;
    }







    /**
     * Synchronize all local streams with remote source
     */
    public void scanAll() {
        Map<StreamKey,StreamEventDto> result = new HashMap<>();

        // 1. ?????????????? ?????? streamDto
        // 2. ??????????????/???????????????? ?????? Stream
        // 3. ??????????????/???????????????? ?????? StreamStatus
        // 4. ???????????????? ?????? ???????????????????????? ???????????? ???????????????? StreamStatus ?????? disabled
        // 5. ???????????????????? ??????????????


        // ????????, ?????? ?? ?????????????? ???? ???????? ???????????????? ???? ???????????? ????????????
        //boolean firstRun = streamMultiService.size() == 0;

        // 1.
        List<StreamDto> dtoList = streamDownloader.getAll();

        // 2.
        List<Stream> updatesStream = streamMapper.toEntityList(dtoList);
        Map<StreamKey,StreamEventDto> u1 = updateStreams(updatesStream);
        mergeEvents(u1, result);

        // 3.
        List<StreamState> updateStates = streamStateMapper.toEntityList(dtoList);
        updateStates.forEach(st -> {
            Set<StreamEventType> events = streamStateMultiService.update(st);
            mergeEvents(st.getStreamKey(), events, result);
        });

        // 4.
        Map<StreamKey,StreamEventDto> u3 = checkExpiredStreamStats(updateStates);
        mergeEvents(u3, result);

        // 5.
        if(!StreamStateMultiService.firstRun) {
            messageSink(new ArrayList<>(result.values()));
        }
    }


    /**
     * Stream was started
     * @param key StreamKey
     */
    public void start(StreamKey key) {
        Map<StreamKey,StreamEventDto> result = new HashMap<>();

        // 1. ??????????????/???????????????? StreamStatus - ?????????????? enable, alive ?????????? ??????????????, ???????? ???? ????????
        // 2. ?????????????? Stream
        // 3. ??????????????/???????????????? Stream
        // 4. ???????????????????? ??????????????


        // 1.
        AtomicBoolean localAlive = new AtomicBoolean(false);
        streamStateMultiService.findByKey(key).ifPresent(local -> localAlive.set(local.isAlive()));
        StreamState stateUpdate = new StreamState(key, true, localAlive.get());

        Set<StreamEventType> events = streamStateMultiService.update(stateUpdate);
        mergeEvents(key, events, result);

        // 2.
        Optional<StreamDto> oDto = streamDownloader.getOne(key);

        // 3.
        oDto.ifPresent(dto -> {
            Stream update = streamMapper.toEntity(dto);
            Map<StreamKey,StreamEventDto> u2 = updateStreams(Collections.singletonList(update));
            mergeEvents(u2, result);
        });
        oDto.orElseThrow(() -> new IllegalArgumentException(
            formatMsg("Downloading stream '" + key + "' - stream not found", key)));

        // 4.
        messageSink(new ArrayList<>(result.values()));
    }


    /**
     * Stop stream
     * @param key streamKey
     */
    public void stop(StreamKey key) {

        // 1. ???????????????? StreamStatus - ?????????????? ?????? enabled = false, alive = ?????????? ???? ????????????????????, ???????? ????????

        Map<StreamKey,StreamEventDto> result = new HashMap<>();

        AtomicBoolean localAlive = new AtomicBoolean(false);
        streamStateMultiService.findByKey(key).ifPresent(local -> localAlive.set(local.isAlive()));
        StreamState stateUpdate = new StreamState(key, false, localAlive.get());

        Set<StreamEventType> events = streamStateMultiService.update(stateUpdate);
        mergeEvents(key, events, result);
        messageSink(new ArrayList<>(result.values()));
    }


    /**
     * Update aliveness of Stream
     */
    @SneakyThrows
    public void changeAlive(StreamKey key, boolean alive) {

        // 1. ???????????????? StreamStatus enabled ?????????? ?????? ????????, alive ?????????? ???? ????????????????????

        // update:
        // 1. ?????????????????? ??????????????, ???????? streamStateMultiService ???? ?????????? ?????????? stream
        // ???????? timeout, ???? ???????????? ???? ???????????????????????? ?? ???? ???????????????????????????? ??????????????.

        Map<StreamKey,StreamEventDto> result = new HashMap<>();

        Optional<StreamState> state = streamStateMultiService.findByKeyWait(key);

        state.ifPresent(local -> {
            StreamState update = new StreamState(key, local.isEnabled(), alive);
            Set<StreamEventType> events = streamStateMultiService.update(update);
            mergeEvents(key, events, result);
            messageSink(new ArrayList<>(result.values()));
        });

//      Map<StreamKey,StreamEventDto> result = new HashMap<>();
//      StreamState update = new StreamState(key, alive, alive);
//      streamStateMultiService.findByKey(key).ifPresent(local -> update.setEnabled(local.isEnabled()));
//      Set<StreamEventType> events = streamStateMultiService.update(update);
//      mergeEvents(key, events, result);
//      messageSink(new ArrayList<>(result.values()));


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
                mergeEvent(key, StreamEventType.CREATED, result);
            }
            // existing stream
            else {
                // .orElseThrow(() -> new IllegalArgumentException(formatMsg("Stream '{}' not found", key)));
                //noinspection OptionalGetWithoutIsPresent
                Stream l = streamMultiService.findByKey(key).get();

                // stream has been changed
                if (!s.equals(l)) {
                    modifiedStreams.add(s);
                    mergeEvent(key, StreamEventType.UPDATED, result);
                }
            }
        });

        // save all new streams
        streamMultiService.saveAll(newStreams);

        // update existing streams
        streamMultiService.saveAll(modifiedStreams);

        // do nothing with deleted streams (that exists only locally)

        // -----------------------------------------------------------------------------------
        // ToDo: ???????????????????????? ???????????????????? ?????????????? StreamMultiService, StreamStateMultiService
        //       ???? ?????????????? ???????????????????? ??????????????, ??.??. ???????????????? ?????????????? ???? ????????????????????
        // -----------------------------------------------------------------------------------

        return result;
    }




//    private synchronized Map<StreamKey,StreamEventDto> updateStreamStates(List<StreamState> updates) {
//        Map<StreamKey,StreamEventDto> result = new HashMap<>();
//
//        updates.forEach(st -> {
//            Set<StreamEventType> events = streamStateMultiService.update(st);
//            mergeEvents(st.getStreamKey(), events, result);
//        });
//
//        return result;
//    }



    // Mediaserver (old version) on stream disabling do not set stream.enabled=false in API
    // but simply remove this whole stream from API response

    /**
     * Mark streams state as Disabled for only locally existing streams
     */
    private Map<StreamKey,StreamEventDto> checkExpiredStreamStats(List<StreamState> updates) {
        Map<StreamKey,StreamEventDto> result = new HashMap<>();

        // Put to Map
        Map<StreamKey,StreamState> updateStateMap = Maps.uniqueIndex(updates, StreamState::getStreamKey);

        // finally find all states that exists only locally and mark them as disabled
        streamStateMultiService.findAll().stream().filter(st -> !updateStateMap.containsKey(st.getStreamKey()))
            .forEach(st -> {
                StreamState update = new StreamState(st.getStreamKey(), false, st.isAlive());
                Set<StreamEventType> events = streamStateMultiService.update(update);
                mergeEvents(st.getStreamKey(), events, result);
            });

        return result;
    }


//    /**
//     * ?????????????????? ?????????????? ?????????? ???????? ??????????????
//     */
//    public void calculateFlap() {
//        streamStateMultiService.findAll().forEach(StreamState::calculateRate);
//    }





    /**
     * Receive here all calculated updates about streams
     */
    private void messageSink(List<StreamEventDto> events) {

//        // filter out stream with flapping
//        events = events.stream().filter(e -> {
//            StreamKey key = e.getKey();
//            AtomicBoolean isFlapping = new AtomicBoolean(false);
//            streamStateMultiService.findByKey(key)
//                .ifPresent(st -> isFlapping.set(st.getFlapRateMoving() > STREAM_FLAPPING_MIN_RATE));
//            return !isFlapping.get();
//        }).collect(Collectors.toList());

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


        log.debug("MONITOR: {}", events);
        streamEventSender.send(events);
    }



    private void mergeEvent(StreamKey key, StreamEventType event, Map<StreamKey,StreamEventDto> map) {


        StreamEventDto tmp = map.putIfAbsent(key, new StreamEventDto(key, Sets.newHashSet(event)));
        if (tmp != null) {
            tmp.getEventSet().add(event);
        }
    }

    private void mergeEvent(StreamEventDto event, Map<StreamKey,StreamEventDto> map) {

        StreamEventDto tmp = map.putIfAbsent(event.getKey(), event);
        if (tmp != null) {
            tmp.getEventSet().addAll(event.getEventSet());
        }
    }

    private void mergeEvents(StreamKey key, Set<StreamEventType> events, Map<StreamKey,StreamEventDto> map) {

        if(events.size() == 0) {
            return;
        }

        StreamEventDto tmp = map.putIfAbsent(key, new StreamEventDto(key, events));
        if (tmp != null) {
            tmp.getEventSet().addAll(events);
        }
    }

    private void mergeEvents(Map<StreamKey, StreamEventDto> source, Map<StreamKey, StreamEventDto> result) {

        if(source.size() == 0) {
            return;
        }

        for (StreamEventDto event : source.values()) {
            StreamKey key = event.getKey();
            if(result.containsKey(key)) {
                result.get(key).getEventSet().addAll(event.getEventSet());
            }
            else {
                result.put(key, event);
            }
        }
    }

//    public void printFlapAll() {
//
//        log.debug("===================== PERIODS ALL =========================");
//        Map<StreamKey, Double> map = streamStateMultiService.getPeriodsAll();
//        for (Map.Entry<StreamKey, Double> entry : map.entrySet()) {
//            log.debug("Stream: {} : {}", entry.getKey(), entry.getValue());
//        }
//        log.debug("============================================================");
//    }


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
