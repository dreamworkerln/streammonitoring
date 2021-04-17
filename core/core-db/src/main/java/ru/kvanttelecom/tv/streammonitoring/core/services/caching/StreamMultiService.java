package ru.kvanttelecom.tv.streammonitoring.core.services.caching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.streammonitoring.core.caches.Cachelevel;
import ru.kvanttelecom.tv.streammonitoring.core.caches.MapCache;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.Multicache;
import ru.kvanttelecom.tv.streammonitoring.core.caches.Index;
import ru.kvanttelecom.tv.streammonitoring.core.services.database.StreamService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.dreamworkerln.spring.utils.common.StringUtils.formatMsg;

@Service
@Slf4j
public class StreamMultiService extends Multicache<Stream> {

    @Autowired
    private StreamService streamService;

    private final Index<StreamKey, Stream> streamKeyIndex = new Index<>(Stream::getStreamKey);


    @Override
    protected List<Cachelevel<Stream>> addLevels() {
        MapCache<Stream> mapCache = new MapCache<>();
        mapCache.addIndex(streamKeyIndex);
        return new ArrayList<>(List.of(mapCache, streamService));
    }


    public Optional<Stream> findByStreamKey(StreamKey key) {
        return streamKeyIndex.findByKey(key);
    }

    public List<Stream> findAllByKey(List<StreamKey> keys) {
        return streamKeyIndex.findAllByKeys(keys);
    }


    public void delete(StreamKey key) {
        Optional<Stream> oStream = streamKeyIndex.findByKey(key);
        if(oStream.isEmpty()) {
            throw new IllegalArgumentException(formatMsg("stream '{}' not found", key));
        }
        delete(oStream.get());
    }
}
