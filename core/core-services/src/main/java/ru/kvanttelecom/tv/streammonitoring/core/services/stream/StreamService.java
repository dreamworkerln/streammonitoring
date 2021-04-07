package ru.kvanttelecom.tv.streammonitoring.core.services.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dreamworkerln.spring.utils.common.configurations.annotations.AutowireClassList;
import ru.kvanttelecom.tv.streammonitoring.core.cache.MultiCache;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services.stream.cache.level.StreamCacheLevelHazelcast;
import ru.kvanttelecom.tv.streammonitoring.core.services.stream.cache.level.StreamCacheLevelDb;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Stream service, use read/write-through hazelcast embedded cache
 */
@Service
@Transactional
@Slf4j
public class StreamService {

    @AutowireClassList({StreamCacheLevelHazelcast.class, StreamCacheLevelDb.class})
    private MultiCache<Long,Stream> cache;

//    /**
//     * Find stream by StreamKey
//     */
//    public Optional<Stream> findById(long id) {
//        return Optional.ofNullable(cache.get(id));
//    }
//

    /**
     * Find Stream by Stream.server.hostName + "." + Stream.name
     * @param hostname   Stream.server.hostName
     * @param streamName Stream.name
     * @return
     */
    public Optional<Stream> findByKey(String hostname, String streamName) {
        return Optional.ofNullable(nameIndex.get(hostname + "." + streamName));
    }

    /**
     * Find stream by Id
     */
    public Optional<Stream> findById(long id) {
        return Optional.ofNullable(cache.get(id));
    }


    public void save(Stream stream) {
        cache.put(stream.getId(), stream);
    }


    public List<Stream> findByKeys(List<Long> keys) {
        return cache.getAll(keys);
    }

    public List<Stream> findAll() {
        return cache.getAll();
    }

    // -------------------------------------------------------------------------------


//
//
//    /**
//     * Find streams by StreamKeys
//     */
//    public Map<StreamKey,Stream> findByKeys(Set<StreamKey> keys) {
//
//        Map<StreamKey,Stream> result = map.getAll(keys);
//        return refillMap(keys, result);
//    }
//
//
//    /**
//     * Find all streams
//     */
//    public Map<StreamKey,Stream> findAll() {
//
//        Map<StreamKey,Stream> result = map.values().stream()
//            .collect(Collectors.toMap(Stream::getStreamKey, Function.identity()));
//
//        Set<StreamKey> keys = result.keySet();
//        return refillMap(keys, result);
//    }
//
//
//    private Map<StreamKey, Stream> refillMap(Set<StreamKey> keys, Map<StreamKey, Stream> result) {
//
//        keys.forEach(key -> {
//            if(!result.containsKey(key)) {
//                Optional<Stream> tmp = repository.findOneByStreamKey(key, null);
//                if(tmp.isPresent()) {
//                    Stream stream = tmp.get();
//
//                    map.put(stream.getStreamKey(), stream);
//                    result.put(stream.getStreamKey(), stream);
//                }
//            }});
//
//        return result;
//    }




    // CACHE LEVELS -----------------------------------------------------------------------------

    



//    private static class CacheStreamFinder implements Finder<StreamKey, Stream> {
//
//        private final IMap<StreamKey, Stream> map;
//
//        private CacheStreamFinder(IMap<StreamKey, Stream> map) {
//            this.map = map;
//        }
//
//
//        @Override
//        public Stream find(StreamKey streamKey) {
//            return map.get(streamKey);
//        }
//    }



//        return names.stream().filter(name -> streams.containsKey(name))
//            .map(streams::get)
//            .collect(Collectors.toList());





//        return names.stream().filter(name -> streams.containsKey(name))
//            .map(streams::get)
//            .collect(Collectors.toList());



//    /**
//     * Apply streams updates from selected server
//     * Store streams updates from selected server to streams and calculate StreamEvents
//     * @param serverName server
//     * @param updates updates from this server
//     * @return calculated StreamEvents from this updates
//     */
//    public List<StreamEventDto> applyUpdate(String serverName, List<MediaServerEvent> updateList) {

//        List<StreamEventDto> result = new ArrayList<>();
//
//        Map<String, MediaServerEvent> updates =
//            updateList.stream().collect(Collectors.toMap(MediaServerEvent::getStreamName, Function.identity()));
//
//
////        // ------------------------------------------------------------------------------------------------------
////        // Append server domain name to stream name as postfix
////        // Experimental -----------------------------------------------------------------------------------------
////        List<StreamUpdate> tmp = new ArrayList<>();
////        updateList.forEach(u -> {
////
////            String[] split = u.getServerName().split("\\.");
////            String srvDomName = "";
////            if(split.length > 0) {
////                srvDomName = "." +split[0];
////            }
////            tmp.add(new StreamUpdate(u.getServerName(), u.getName() + srvDomName, u.getTitle(), u.isAlive()));
////        });
////
////         updates =  tmp.stream().collect(Collectors.toMap(StreamUpdate::getName, Function.identity()));
////        // Experimental -----------------------------------------------------------------------------------------
////
//
//
//
//        // Обход по всем стримам на одном сервере
//        try {
//            // Карта стримов на выбранном сервере
//            StreamMap serverStreams = servers.get(serverName);
//
//            // Детектирует состояние первоначальной загрузки стримов со стримера -
//            // когда для выбранного сервера еще ни одного стрима не было загружено
//            // соответственно надо создать событие что стрим был инициализирован, а не добавлен.
//            // Иначе при 1 запуске по всем работающим стримам будет создано событие что они были добавлены
//            //
//            // (под добавлением имеется ввиду событие, когда пользователь добавил вручную
//            // новый стрим на сервер черев вебинтерфейс flussonic watcher/ flussonic media server)
//
//            boolean serverHasStream = serverStreams.size() > 0;
//
//            for (MediaServerEvent update : updates.values()) {
//
//                String streamName = update.getStreamName();
//                Stream stream = serverStreams.get(streamName);
//
//                // NEW STREAM - not found locally - add new stream to servers.streams
//                if (stream == null) {
//
//                    // create new stream from StreamUpdate
//                    stream = updateConverter.toStream(update);
//
//                    // add stream to stream - добавляем стрим на сервер
//                    serverStreams.put(stream.getName(), stream);
//
//                    // add stream to streams - добавляем стрим в общий список стримов
//                    streams.put(stream.getName(), stream);
//
//                    Set<StreamEventType> typeList = new HashSet<>();
//
//                    // ADD STREAM
//                    if (serverHasStream) {
//                        typeList.add(StreamEventType.ADDED);
//                        log.debug("{} STREAM: {} {}",typeList, stream.getName(), stream.getTitle());
//                    }
//                    // INIT STREAM
//                    else {
//                        typeList.add(StreamEventType.INIT);
//                        //log.trace("{} STREAM: {} {}",typeList, stream.getName(), stream.getTitle());
//                    }
//
//
//                    // create notification that stream has been added/inited
//                    StreamEventDto streamEventDto = new StreamEventDto(1, streamName, typeList);
//                    result.add(streamEventDto);
//
//                }
//                // STREAM STATUS CHANGED - existing stream found - check stream status modifications
//                else {
//
//                    Set<StreamEventType> typeList = calcStreamlternation(stream, update);
//
//                    // If stream status has been changed
//                    if (typeList.size() > 0) {
//                        StreamEventDto event = new StreamEventDto(1, streamName, typeList);
//                        result.add(event);
//
//                        log.debug("{} STREAM: {} {}",typeList, stream.getName(), stream.getTitle());
//                    }
//                }
//            }
//
//            // DELETED STREAM
//            for (Map.Entry<String, Stream> entry : serverStreams.entrySet()) {
//
//                String streamName = entry.getKey();
//                Stream stream = entry.getValue();
//
//                // Если у нас в serverStreams есть стрим, а в обновлении ее нет,
//                // Значит стрим был удален с flussonic watcher
//                if (!updates.containsKey(streamName)) {
//
//                    // removing stream from serverStreams
//                    serverStreams.remove(streamName);
//
//                    // removing stream from streams - удаляем стрим из общего списка стримов
//                    streams.remove(streamName);
//
//                    Set<StreamEventType> typeList = new HashSet<>();
//                    typeList.add(StreamEventType.DELETED);
//
//                    StreamEventDto event = new StreamEventDto(1, streamName, typeList);
//                    result.add(event);
//
//                    log.debug("{} STREAM: {} {}",typeList, stream.getName(), stream.getTitle());
//                }
//            }
//        }
//        // try-catch used only to write error message to log, rethrowing
//        catch (Exception rethrow) {
//            log.error("Applying update from MediaServer {} error:", serverName);
//            throw rethrow;
//        }
//        return result;
//    }




    // ===================================================================================================

//    /**
//     * Check if stream status has been changed (add/update/delete)
//     */
//    private Set<StreamEventType> calcStreamlternation(Stream stream, MediaServerEvent event) {
//
//        Set<StreamEventType> result = new HashSet<>();
//
//        StreamState state = stream.getState();
//
//        // Calculating stream flapping -------------------------------
//
//        Double flappingRate = state.calculateUpdateAliveFreq(event);
//
//        //log.info("Частота изменения StreamUpdate.alive: {}", updateAliveFreq);
//
//        if(flappingRate != null) {
//            log.info("ИЗМЕНЕНИЕ: Частота изменения StreamUpdate.alive: {}", flappingRate);
//
//            if(!stream.isFlapping() && flappingRate > STREAM_FLAPPING_MIN_RATE) {
//                stream.setFlapping(true);
//                result.add(StreamEventType.START_FLAPPING);
//            }
//
//            if(stream.isFlapping() && flappingRate < STREAM_FLAPPING_MIN_RATE) {
//                stream.setFlapping(false);
//                result.add(StreamEventType.STOP_FLAPPING);
//            }
//        }
//
//        // Calculating stream online/offline changing -------------
//
//        // Если update.alive изменилось
//        boolean alive = event.getEventType() == MediaServerEventType.SOURCE_READY ||
//            event.getEventType() == MediaServerEventType.STREAM_STARTED;
//
//        int step = alive ? +1 : -1;
//
//
//        // update stream.level
//        if(Math.abs(state.getLevel() + step) < STREAM_MAX_LEVEL) {
//            state.setLevel(state.getLevel() + step);
//        }
//
//        if(stream.isAlive() && state.getLevel() < -STREAM_THRESHOLD_LEVEL) {
//            stream.setAlive(false);
//            result.add(StreamEventType.OFFLINE);
//        }
//
//        if(!stream.isAlive() && state.getLevel() > +STREAM_THRESHOLD_LEVEL) {
//            stream.setAlive(true);
//            result.add(StreamEventType.ONLINE);
//        }
//
//        return result;
//    }



//    @PostConstruct
//    private void postConstruct() {
//
//        MultiCacheBuilder<StreamKey,Stream> builder = MultiCacheBuilder.getBuilder();
//        builder.addLevel(streamCacheLevelHazelcast)
//            .addLevel(streamCacheLevelDb)
//            .build(streamKey -> {
//                log.warn("NO CacheLoader for StreamService.MultiCache specified");
//                return null;
//            });
//    }






}
