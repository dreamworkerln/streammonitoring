package ru.kvanttelecom.tv.streammonitoring.core.entities.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

@Component
@Slf4j
public class StreamPersistListener {

//    // Updating
//    @PostPersist
//    @PostUpdate
//    public void methodExecuteBeforeSave(Stream stream) {
//
//        // update StreamKey
//        log.trace("Stream post persist/update: {}", stream);
//
//        Long serverId = stream.getServer().getId();
//        Long streamId = stream.getServer().getId();
//
//        if(!serverId.equals(stream.getServer().getId()) ||
//            !streamId.equals(stream.getId())) {
//
//            stream.setStreamKey(StreamKey.from(serverId, streamId));
//        }
//    }

    @PostLoad
    @PostUpdate
    @PostPersist
    public void methodExecuteBeforeSave(Stream stream) {
        stream.initialize();
    }
}
