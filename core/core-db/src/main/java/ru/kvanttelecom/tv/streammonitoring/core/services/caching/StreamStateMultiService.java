package ru.kvanttelecom.tv.streammonitoring.core.services.caching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.dreamworkerln.spring.utils.common.Utils;
import ru.kvanttelecom.tv.streammonitoring.core.caches.Cachelevel;
import ru.kvanttelecom.tv.streammonitoring.core.caches.Index;
import ru.kvanttelecom.tv.streammonitoring.core.caches.MapCache;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.Multicache;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StreamStateMultiService extends Multicache<StreamState> {

    // all streams states
    private final Index<StreamKey, StreamState> streamKeyIndex = new Index<>(StreamState::getStreamKey);

    // streams with problems
    private final Index<StreamKey, StreamState> problemIndex = new Index<>(StreamState::getStreamKey);



    @Override
    protected List<Cachelevel<StreamState>> addLevels() {
        problemIndex.setAutoAddition(false);

        MapCache<StreamState> mapCache = new MapCache<>();
        //mapCache.setAutogenId(true);
        mapCache.addIndex(streamKeyIndex);
        mapCache.addIndex(problemIndex);
        return new ArrayList<>(List.of(mapCache));
    }





    public Optional<StreamState> findByKey(StreamKey key) {
        return streamKeyIndex.findByKey(key);
    }


    /**
     * Update stream status and calculate events
     * @param update new stream state
     * @return calculated events
     * Не вздумай пихать сюда StreamState, взятый из StreamStateMultiService!
     * Обязательно создавай new StreamState(...) иначе никаких изменений не будет
     * Так как получится что это будет один и тот же объект.
     */
    public Set<StreamEventType> update(StreamState update) {

        // result may contain empty HashSet (no generated events at all)
        Set<StreamEventType> result = new HashSet<>();

        Assert.notNull(update, "update == null");
        Assert.notNull(update.getStreamKey(), "update.key == null");

        StreamKey key = update.getStreamKey();



        boolean updateEnabled = update.isEnabled();
        boolean updateAlive = update.isAlive();

        StreamState local;
        // local exists
        if (streamKeyIndex.containsKey(key)) {
            //noinspection OptionalGetWithoutIsPresent
            local = streamKeyIndex.findByKey(key).get();
        }
        // local doesn't exists
        else {
            local = new StreamState(key, !updateEnabled, !updateAlive);
        }
        
        boolean localEnabled = local.isEnabled();
        boolean localAlive = local.isAlive();

        // stream was disabled
        if (localEnabled && !updateEnabled) {
            local.setEnabled(false);
            result.add(StreamEventType.DISABLED);
        }

        // stream was enabled
        if (!localEnabled && updateEnabled) {
            result.add(StreamEventType.ENABLED);
            local.setEnabled(true);
        }

        // stream went down
        if (localAlive && !updateAlive) {
            local.setAlive(false);
            problemIndex.save(local);
            result.add(StreamEventType.OFFLINE);
        }

        // stream going up
        if (!localAlive && updateAlive) {
            local.setAlive(true);
            problemIndex.delete(local);
            result.add(StreamEventType.ONLINE);
        }
        

        // save stream state
        if (result.size() > 0) {
            super.save(local);
        }

        return result;
    }


    /**
     * Get currently offline streams
     */
    public List<StreamState> getOffline() {
        // filter out disabled streams
        return problemIndex.findAll().stream()
            .filter(AbstractEntity::isEnabled).collect(Collectors.toList());
    }

    /**
     * Get currently disabled streams
     */
    public List<StreamState> getDisabled() {
        return streamKeyIndex.findAll().stream()
            .filter(Predicate.not(AbstractEntity::isEnabled)).collect(Collectors.toList());
    }


    @Override
    public StreamState save(StreamState streamState) {
        throw new UnsupportedOperationException("Use update(...)");
    }

    @Override
    public List<StreamState> saveAll(Iterable<StreamState> list) {
        throw new UnsupportedOperationException("Use update(...) in iterator");
    }

    public boolean containsKey(StreamKey key) {
        return streamKeyIndex.containsKey(key);
    }
}



   /*


    public StreamState get(StreamKey key) {
        return map.get(key);
    }


    *//**
 * Add new Stream
 * <br> Stream should have uniq StreamKey
 *//*
    public void put(StreamKey key, boolean alive) {

        Assert.notNull(key, "key == null");

        if(map.containsKey(key)) {
            log.warn("Put stream '" + key + "' - already exists, replacing");
        }

        StreamState state = new StreamState(key, alive);
        map.put(key, state);

        // add to problems
        if(!state.isAlive()) {
            problems.put(key, state);
        }
    }

    public void delete(StreamKey key) {
        Assert.notNull(key, "key == null");

        map.remove(key);
        // remove from problem index (if exists)
        problems.remove(key);
    }

    *//**
 * Update stream state
 * <br>Will trigger events only on special conditions
 *//*
    public Set<StreamEventType> update(StreamKey key, boolean updateAlive) {

        Assert.notNull(key, "key == null");

        Set<StreamEventType> result = new HashSet<>();
        StreamState state = map.get(key);

        if(state == null) {
            log.warn("Updating status of stream '" + key + "'- stream not found, aborting");
            return result;
        }
        boolean localAlive = state.isAlive();


        // stream going down
        if(localAlive && !updateAlive) {
            result.add(StreamEventType.OFFLINE);
            problems.put(key, state);

            // change stream state
            state.setAlive(false);
        }

        // stream going up
        if(!localAlive && updateAlive) {
            result.add(StreamEventType.ONLINE);
            problems.remove(key);

            // change stream state
            state.setAlive(true);
        }
        return result;
    }

*/
