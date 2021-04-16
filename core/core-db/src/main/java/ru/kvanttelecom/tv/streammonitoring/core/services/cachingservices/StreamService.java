package ru.kvanttelecom.tv.streammonitoring.core.services.cachingservices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.BaseCachingService;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.Index;
import ru.kvanttelecom.tv.streammonitoring.core.services.database.StreamDatabaseService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StreamService extends BaseCachingService<Stream> {

    private final StreamDatabaseService streamDatabaseService;

    Index<StreamKey, Stream> streamKeyIndex = new Index<>(Stream::getStreamKey);



    @Autowired
    public StreamService(StreamDatabaseService streamDatabaseService) {
        super(streamDatabaseService);
        addIndex(streamKeyIndex);

        this.streamDatabaseService = streamDatabaseService;
    }

    public Optional<Stream> findByStreamKey(StreamKey key) {
        return Optional.ofNullable(streamKeyIndex.get(key));
    }

    public List<Stream> findAllByKey(List<StreamKey> keys) {
        return streamKeyIndex.getByKeys(keys);
    }

    public void delete(StreamKey key) {
        Stream stream = streamKeyIndex.get(key);
        Assert.notNull(stream, "stream == null, not found by key: '" +key + "'");
        delete(stream);
    }
}
