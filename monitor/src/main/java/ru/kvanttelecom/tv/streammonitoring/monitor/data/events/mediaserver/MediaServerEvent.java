package ru.kvanttelecom.tv.streammonitoring.monitor.data.events.mediaserver;

import lombok.Getter;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.enums.MediaServerEventType;


import java.time.Instant;


/**
 * Stream status update
 * (data, grabbed from flussonic media server, contained current stream status)
 */
public class MediaServerEvent {

    @Getter
    private final MediaServerEventType eventType;

    @Getter
    private final String hostname;

    @Getter
    private final String name;

    @Getter
    private final Instant time;

    @Getter
    private final String reason;

    @Getter
    private final StreamKey streamKey;

    public MediaServerEvent(MediaServerEventType eventType, String hostname, String name, Instant time, String reason) {
        this.eventType = eventType;
        this.hostname = hostname;
        this.name = name;
        this.time = time;
        this.reason = reason;

        this.streamKey = new StreamKey(hostname, name);
    }



    @Override
    public String toString() {
        return "MediaServerEvent{" +
            "eventType=" + eventType +
            ", serverName='" + hostname + '\'' +
            ", streamName='" + name + '\'' +
            ", time=" + time +
            ", reason='" + reason + '\'' +
            '}';
    }
}


/*


[..., {"event":"source_lost", "client_count":0,"count":0,"event_id":138714527,"limit":10,"loglevel":"info","media":"ag-9177423","reason":"source_down","server":"w1","url":"mbr://","utc_ms":1616694653999}
[..., {"event":"source_ready","event_id":138743411,"loglevel":"info","media":"ag-8570015","server":"w1","source_dts":1616694714431.0,"url":"mbr://","utc_ms":1616694719377}
[..., {"event":"stream_stopped","event_id":67789,"loglevel":"info","media":"ghgfhgfhgfh","server":"T8","utc_ms":1616694674294}
[..., {"event":"stream_started","event_id":67818,"loglevel":"info","media":"ghgfhgfhgfh","server":"T8","utc_ms":1616694675574}


 */
