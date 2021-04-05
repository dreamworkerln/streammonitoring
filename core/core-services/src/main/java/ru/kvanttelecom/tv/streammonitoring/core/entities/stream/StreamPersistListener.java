package ru.kvanttelecom.tv.streammonitoring.core.entities.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
}
