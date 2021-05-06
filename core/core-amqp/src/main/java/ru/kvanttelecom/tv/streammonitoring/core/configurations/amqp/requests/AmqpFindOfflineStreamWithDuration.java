package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;

import java.time.Duration;

@Data
public class AmqpFindOfflineStreamWithDuration extends AmqpRequest {

    private final Duration duration;

    @JsonCreator
    public AmqpFindOfflineStreamWithDuration(Duration duration) {
        this.duration = duration;
    }
}
