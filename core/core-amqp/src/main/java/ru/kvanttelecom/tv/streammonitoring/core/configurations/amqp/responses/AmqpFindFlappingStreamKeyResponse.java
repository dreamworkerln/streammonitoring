package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.Getter;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.deserializer.StreamKeyDeserializer;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;

import java.util.Map;

@Data
public class AmqpFindFlappingStreamKeyResponse extends AmqpResponse {

    @JsonDeserialize(keyUsing = StreamKeyDeserializer.class)
    private final Map<StreamKey,Double> map;

    @JsonCreator
    public AmqpFindFlappingStreamKeyResponse(Map<StreamKey, Double> map) {
        this.map = map;
    }
}
