package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;

import java.util.List;

@Data
public class AmqpFindAllStreamByKey extends AmqpRequest {
    private final Iterable<StreamKey> keys;

    @JsonCreator
    public AmqpFindAllStreamByKey(Iterable<StreamKey> keys) {
        this.keys = keys;
    }
}
