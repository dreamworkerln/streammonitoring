package ru.kvanttelecom.tv.streammonitoring.monitor.services.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.enums.MediaServerEventType;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.events.mediaserver.MediaServerEvent;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamEventType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class StreamStateService {

    // all streams states
                            // <StreamKey, StreamState>
    private final ConcurrentMap<StreamKey, StreamState> map = new ConcurrentHashMap<>();

    // all streams with problems
    private final ConcurrentMap<StreamKey, StreamState> problems = new ConcurrentHashMap<>();

//
//
//
//    @Autowired
//    private StreamService streamService;
//
//    @Autowired
//    private StreamManager importer;





    /**
     * Add new Stream
     * <br> Stream should have uniq StreamKey
     */
    public void put(Stream stream) {
        StreamKey key = stream.getStreamKey();

        if(map.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate StreamState - already contains StreamStatus for " + key);
        }

        StreamState state = new StreamState(key);
        state.setAlive(stream.isInitialAliveInternal());
        map.put(key, state);

        // add to problems
        if(!state.isAlive()) {
            problems.put(key, state);
        }
    }

//    public StreamState get(Stream stream) {
//        String key = stream.getStreamKey();
//        return map.get(key);
//    }


    public void delete(Stream stream) {
        delete(stream.getStreamKey());
    }

    public void delete(StreamKey key) {
        map.remove(key);
        // remove from problem index (if exists)
        problems.remove(key);
    }

    // Process stream state
    public Set<StreamEventType> updateAlive(boolean updateAlive) {

        Set<StreamEventType> result = new HashSet<>();

        // validation
        //
        // Here event.type should be only [SOURCE_READY,SOURCE_LOST]
        if(event.getEventType() != MediaServerEventType.SOURCE_READY &&
            event.getEventType() != MediaServerEventType.SOURCE_LOST) {
            throw new IllegalArgumentException("Bad event.type: " + event.getEventType());
        }

        boolean updateAlive = event.getEventType() == MediaServerEventType.SOURCE_READY;

        StreamKey key = event.getStreamKey();
        StreamState state = map.get(key);

        // change stream state
        state.setAlive(updateAlive);

        if(updateAlive) {
            result.add(StreamEventType.ONLINE);
            problems.remove(key);
        }
        else {
            result.add(StreamEventType.OFFLINE);
            problems.put(key, state);
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
