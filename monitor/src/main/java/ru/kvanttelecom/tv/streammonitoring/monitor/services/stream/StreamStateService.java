package ru.kvanttelecom.tv.streammonitoring.monitor.services.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services.stream.StreamService;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.events.mediaserver.MediaServerEvent;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.StreamImporter;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.downloader.StreamDownloader;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class StreamStateService {

    // all streams states
                            // <StreamKey, StreamState>
    private final ConcurrentMap<String, StreamState> map = new ConcurrentHashMap<>();

    // all streams with problems
    private final ConcurrentMap<String, StreamState> problems = new ConcurrentHashMap<>();




    @Autowired
    private StreamService streamService;

    @Autowired
    private StreamImporter importer;





    /**
     * Add new Stream
     * <br> Stream should have uniq StreamKey
     */
    public void put(Stream stream) {
        String key = stream.getStreamKey();

        if(map.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate StreamState - already contains StreamStatus for " + key);
        }

        StreamState state = new StreamState();
        state.setAlive(stream.isInitialStateAlive());
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


    public void remove(Stream stream) {
        String key = stream.getStreamKey();
        map.remove(key);
        // remove from problem index (if exists)
        problems.remove(key);
    }

    // Process stream state
    public void process(Stream stream) {
        throw new NotImplementedException();
    }



    // -----------------------------------------------------------------------------------------------------------------









}
