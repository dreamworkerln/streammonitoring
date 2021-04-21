package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;


@Data
public class AmqpErrorResponse extends AmqpResponse {
    private final String message;

    @JsonCreator
    public AmqpErrorResponse(String message) {
        this.message = message;
    }
}
