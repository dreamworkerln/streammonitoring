package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests;

import lombok.Data;

public class AmqpFindAllStream extends AmqpRequest {
    @Override
    public String toString() {
        return "FIND_ALL_STREAMS";
    }
}
