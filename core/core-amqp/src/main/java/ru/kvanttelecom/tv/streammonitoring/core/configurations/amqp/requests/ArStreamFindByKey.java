package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;


@EqualsAndHashCode(callSuper = true)
@Data
public class ArStreamFindByKey extends ArAbstract {

    private final StreamKey key;

    @JsonCreator
    public ArStreamFindByKey(@JsonProperty("key")StreamKey key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "GET_STREAM_BY_KEY";
    }
}
