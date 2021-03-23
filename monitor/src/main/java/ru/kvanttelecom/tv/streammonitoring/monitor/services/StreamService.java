package ru.kvanttelecom.tv.streammonitoring.monitor.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;
import ru.kvanttelecom.tv.streammonitoring.utils.converter.StreamUpdateConverter;
import ru.kvanttelecom.tv.streammonitoring.utils.data.Stream;
import ru.kvanttelecom.tv.streammonitoring.utils.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.utils.data.StreamUpdate;
import ru.kvanttelecom.tv.streammonitoring.utils.entities.StreamMap;
import ru.kvanttelecom.tv.streammonitoring.monitor.entities.ServerMap;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.StreamEvent;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StreamService {

    private static final int STREAM_MAX_LEVEL = 10;
    private static final int STREAM_THRESHOLD_LEVEL = (int)(STREAM_MAX_LEVEL * 0.7);

    private static final Double STREAM_FLAPPING_MIN_RATE = 1./600; // 1 раз в 10 мин

    // минимально детектируемая частота флапа стрима (фильтр высоких частот флапа стрима)
    //private double streamFlappingMinRate;

    @Autowired
    private StreamMap streams;

    @Autowired
    private ServerMap servers;

    @Autowired
    private StreamUpdateConverter updateConverter;

    @Autowired
    private MonitorProperties props;

    private void postConstruct() {
        //streamFlappingMinRate = STREAM_FLAPPING_MIN_RATE;
    }


    public List<Stream> findAll() {
        return new ArrayList<>(streams.getMap().values());
    }


    /**
     * Get streams that have specified names
     */
    public List<Stream> findById(List<String> names) {

        return names.stream().filter(name -> streams.containsKey(name))
            .map(streams::get)
            .collect(Collectors.toList());
    }


    /**
     * Apply streams updates from selected server
     * Store streams updates from selected server to streams and calculate StreamEvents
     * @param serverName server
     * @param updates updates from this server
     * @return calculated StreamEvents from this updates
     */
    public List<StreamEvent> applyUpdate(String serverName, List<StreamUpdate> updateList) {

        List<StreamEvent> result = new ArrayList<>();

        Map<String, StreamUpdate> updates =
            updateList.stream().collect(Collectors.toMap(StreamUpdate::getName, Function.identity()));


        // Обход по всем стримам на одном сервере
        try {
            // Карта стримов на выбранном сервере
            StreamMap serverStreams = servers.get(serverName);

            // Детектирует состояние первоначальной загрузки стримов со стримера -
            // когда для выбранного сервера еще ни одного стрима не было загружено
            // соответственно надо создать событие что стрим был инициализирован, а не добавлен.
            // Иначе при 1 запуске по всем работающим стримам будет создано событие что они были добавлены
            //
            // (под добавлением имеется ввиду событие, когда пользователь добавил вручную
            // новый стрим на сервер черев вебинтерфейс flussonic watcher/ flussonic media server)

            boolean serverHasStream = serverStreams.size() > 0;

            for (StreamUpdate update : updates.values()) {

                String streamName = update.getName();
                Stream stream = serverStreams.get(streamName);

                // NEW STREAM - not found locally - add new stream to servers.streams
                if (stream == null) {

                    // create new stream from StreamUpdate
                    stream = updateConverter.toStream(update);

                    // add stream to stream - добавляем стрим на сервер
                    serverStreams.put(stream.getName(), stream);

                    // add stream to streams - добавляем стрим в общий список стримов
                    streams.put(stream.getName(), stream);

                    Set<StreamEventType> typeList = new HashSet<>();

                    // ADD STREAM
                    if (serverHasStream) {
                        typeList.add(StreamEventType.ADDED);
                        log.debug("{} STREAM: {} {}",typeList, stream.getName(), stream.getTitle());
                    }
                    // INIT STREAM
                    else {
                        typeList.add(StreamEventType.INIT);
                        //log.trace("{} STREAM: {} {}",typeList, stream.getName(), stream.getTitle());
                    }


                    // create notification that stream has been added/inited
                    StreamEvent streamEvent = new StreamEvent(streamName, typeList);
                    result.add(streamEvent);

                }
                // STREAM STATUS CHANGED - existing stream found - check stream status modifications
                else {

                    Set<StreamEventType> typeList = calcStreamlternation(stream, update);

                    // If stream status has been changed
                    if (typeList.size() > 0) {
                        StreamEvent event = new StreamEvent(streamName, typeList);
                        result.add(event);

                        log.debug("{} STREAM: {} {}",typeList, stream.getName(), stream.getTitle());
                    }
                }
            }

            // DELETED STREAM
            for (Map.Entry<String, Stream> entry : serverStreams.entrySet()) {

                String streamName = entry.getKey();
                Stream stream = entry.getValue();

                // Если у нас в serverStreams есть стрим, а в обновлении ее нет,
                // Значит стрим был удален с flussonic watcher
                if (!updates.containsKey(streamName)) {

                    // removing stream from serverStreams
                    serverStreams.remove(streamName);

                    // removing stream from streams - удаляем стрим из общего списка стримов
                    streams.remove(streamName);

                    Set<StreamEventType> typeList = new HashSet<>();
                    typeList.add(StreamEventType.DELETED);

                    StreamEvent event = new StreamEvent(streamName, typeList);
                    result.add(event);

                    log.debug("{} STREAM: {} {}",typeList, stream.getName(), stream.getTitle());
                }
            }
        }
        // try-catch used only to write error message to log, rethrowing
        catch (Exception rethrow) {
            log.error("Applying update from MediaServer {} error:", serverName);
            throw rethrow;
        }
        return result;
    }




    // ===================================================================================================

    /**
     * Check if stream status has been changed (add/update/delete)
     */
    private Set<StreamEventType> calcStreamlternation(Stream stream, StreamUpdate update) {

        Set<StreamEventType> result = new HashSet<>();

        StreamState state = stream.getState();

        // Calculating stream flapping -------------------------------

        Double flappingRate = state.calculateUpdateAliveFreq(update);

        //log.info("Частота изменения StreamUpdate.alive: {}", updateAliveFreq);

        if(flappingRate != null) {
            log.info("ИЗМЕНЕНИЕ: Частота изменения StreamUpdate.alive: {}", flappingRate);

            if(!stream.isFlapping() && flappingRate > STREAM_FLAPPING_MIN_RATE) {
                stream.setFlapping(true);
                result.add(StreamEventType.START_FLAPPING);
            }

            if(stream.isFlapping() && flappingRate < STREAM_FLAPPING_MIN_RATE) {
                stream.setFlapping(false);
                result.add(StreamEventType.STOP_FLAPPING);
            }
        }

        // Calculating stream online/offline changing -------------
        int step = update.isAlive() ? +1 : -1;


        // update stream.level
        if(Math.abs(state.getLevel() + step) < STREAM_MAX_LEVEL) {
            state.setLevel(state.getLevel() + step);
        }

        if(stream.isAlive() && state.getLevel() < -STREAM_THRESHOLD_LEVEL) {
            stream.setAlive(false);
            result.add(StreamEventType.OFFLINE);
        }

        if(!stream.isAlive() && state.getLevel() > +STREAM_THRESHOLD_LEVEL) {
            stream.setAlive(true);
            result.add(StreamEventType.ONLINE);
        }

        return result;
    }

}
