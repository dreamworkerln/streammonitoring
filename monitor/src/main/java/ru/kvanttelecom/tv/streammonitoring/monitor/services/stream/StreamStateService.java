package ru.kvanttelecom.tv.streammonitoring.monitor.services.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.BaseCachingService;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.BaseRepoAccessService;
import ru.kvanttelecom.tv.streammonitoring.monitor.repositories.FakeStreamStateRepository;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class StreamStateService extends BaseCachingService<Server> {

    // all streams states
                            // <StreamKey, StreamState>
    private final ConcurrentMap<StreamKey, StreamState> map = new ConcurrentHashMap<>();

    // all streams with problems
    private final ConcurrentMap<StreamKey, StreamState> problems = new ConcurrentHashMap<>();



    public StreamStateService(FakeBaseRepoAccessService fakeBaseRepoAccessService) {
        super(fakeRepository);
    }


    public StreamState get(StreamKey key) {
        return map.get(key);
    }


    /**
     * Add new Stream
     * <br> Stream should have uniq StreamKey
     */
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

    /**
     * Update stream state
     * <br>Will trigger events only on special conditions
     */
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


    public List<StreamState> getOffline() {
        return new ArrayList<>(problems.values());
    }



    // -----------------------------------------------------------------------------------------------------------------

    public int size() {
        return map.size();
    }


}
