package ru.kvanttelecom.tv.streammonitoring.core.services.caching;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.streammonitoring.core.caches.Cachelevel;
import ru.kvanttelecom.tv.streammonitoring.core.caches.Index;
import ru.kvanttelecom.tv.streammonitoring.core.caches.MapCache;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.Multicache;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamStateTypes;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.kvanttelecom.tv.streammonitoring.core.data.SubState.STREAM_FLAPPING_MAX_PERIOD_SECONDS;


// Predicate.not()


@Service
@Slf4j
public class StreamStateMultiService extends Multicache<StreamState> {


    // Marker that system have not been initialized with new data
    public static boolean firstRun = true;



    // all streams states
    private final Index<StreamKey, StreamState> streamKeyIndex = new Index<>(StreamState::getStreamKey);

    // streams with problems
    private final Index<StreamKey, StreamState> offlineIndex = new Index<>(StreamState::getStreamKey);
    // streams with problems
    private final Index<StreamKey, StreamState> flappingIndex = new Index<>(StreamState::getStreamKey);


    private final ConcurrentMap<StreamKey, Object> statusWaits = new ConcurrentHashMap<>();



    // pattern method - initializing additional indexes
    @Override
    protected List<Cachelevel<StreamState>> addLevels() {

        offlineIndex.setAutoAddition(false);
        flappingIndex.setAutoAddition(false);

        MapCache<StreamState> mapCache = new MapCache<>();
        mapCache.addIndex(streamKeyIndex);
        mapCache.addIndex(offlineIndex);
        mapCache.addIndex(flappingIndex);
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

            boolean localEnabled = local.isEnabled();
            boolean localAlive = local.isAlive();

            boolean res;

            //local.setEnabled(false);

            // stream was disabled
            if (localEnabled && !updateEnabled) {
                res = local.update(StreamStateTypes.ENABLENESS, false);
                if(res) {
                    result.add(StreamEventType.DISABLED);
                }
            }

            // stream was enabled
            if (!localEnabled && updateEnabled) {
                res = local.update(StreamStateTypes.ENABLENESS, true);
                if(res) {
                    result.add(StreamEventType.ENABLED);
                }
            }

            // stream went down
            if (localAlive && !updateAlive) {
                res = local.update(StreamStateTypes.ALIVENESS, false);
                offlineIndex.save(local);
                if(res) {
                    result.add(StreamEventType.OFFLINE);
                }
            }

            // stream going up
            if (!localAlive && updateAlive) {
                res = local.update(StreamStateTypes.ALIVENESS, true);
                offlineIndex.delete(local);
                if(res) {
                    result.add(StreamEventType.ONLINE);
                }
            }

            // save stream state if have any changes with it
            if (result.size() > 0) {
                super.save(local);
            }
        }
        // local doesn't exists
        // Initialize new StreamState
        else {

            local = new StreamState(key, updateEnabled, updateAlive);
            super.save(local);

            if(!updateAlive) {
                offlineIndex.save(local);
            }

            // notify all threads waiting for this StreamState
            Object lock = statusWaits.get(key);
            if(lock != null) {
                synchronized (lock) {
                    lock.notifyAll();
                    statusWaits.remove(key);
                }
            }
        }

        return result;
    }


    /**
     * Get currently offline streams that not flapping
     */
    public List<StreamState> getOffline() {
        // filter out disabled streams
        //return offlineIndex.findAll();

        return offlineIndex.findAll().stream()
            .filter(st -> st.getPeriod() > STREAM_FLAPPING_MAX_PERIOD_SECONDS)
            .collect(Collectors.toList());

    }

    /**
     * Get currently flapping streams 
     * (filtered out streams with period > STREAM_FLAPPING_MAX_PERIOD_SECONDS)
     */
    public Map<StreamKey, Double> getPeriods() {
        return streamKeyIndex.findAll().stream()
            .filter(st -> st.getPeriod() <= STREAM_FLAPPING_MAX_PERIOD_SECONDS)
            .collect(Collectors.toMap(StreamState::getStreamKey, StreamState::getPeriod));

    }

    public Map<StreamKey, Double> getPeriodsAll() {
        return streamKeyIndex.findAll().stream()
            .collect(Collectors.toMap(StreamState::getStreamKey, StreamState::getPeriod));
    }



    /**
     * Get currently disabled streams
     */
    public List<StreamState> getDisabled() {
        return streamKeyIndex.findAll().stream()
            .filter(Predicate.not(StreamState::isEnabled)).collect(Collectors.toList());
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

    @SneakyThrows
    public Optional<StreamState> findByKeyWait(StreamKey key) {

        Optional<StreamState> result = streamKeyIndex.findByKey(key);

        if(result.isEmpty()) {
            // wait for streamKeyIndex update with timeout
            Object lock = statusWaits.computeIfAbsent(key, sk -> new Object());
            synchronized (lock) {
                lock.wait(1000);
            }
            result = streamKeyIndex.findByKey(key);
        }
        return result;
    }

}



   /*

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
