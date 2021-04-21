package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests;


public class AmqpFindOfflineStream extends AmqpRequest {

    @Override
    public String toString() {
        return "FIND_STREAMS_OFFLINE";
    }
}
