package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;

import java.util.List;

@Data
public class AmqpStreamListResponse extends AmqpResponse {
    private final List<StreamDto> list;

    @JsonCreator
    public AmqpStreamListResponse(List<StreamDto> list) {
        this.list = list;
    }
}
