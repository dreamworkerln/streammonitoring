package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;

import java.util.List;

@Data
public class AmqpStreamKeyListResponse extends AmqpResponse {
    private List<StreamKey> list;

    @JsonCreator
    public AmqpStreamKeyListResponse(List<StreamKey> list) {
        this.list = list;
    }
}
