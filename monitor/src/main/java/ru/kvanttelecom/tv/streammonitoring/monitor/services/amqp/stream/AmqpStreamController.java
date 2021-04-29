package ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp.stream;

import org.springframework.beans.factory.annotation.Autowired;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.annotations.AmqpController;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.annotations.AmqpMethod;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.AmqpFindAllStreamByKey;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.AmqpFindFlappingStream;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.AmqpFindOfflineStream;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses.AmqpFindFlappingStreamKeyResponse;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses.AmqpStreamKeyListResponse;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses.AmqpStreamListResponse;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.mappers.stream.StreamMapper;
import ru.kvanttelecom.tv.streammonitoring.core.services.caching.StreamMultiService;
import ru.kvanttelecom.tv.streammonitoring.core.services.caching.StreamStateMultiService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AmqpController
public class AmqpStreamController {

    @Autowired
    private StreamMultiService streamMultiService;

    @Autowired
    private StreamStateMultiService streamStateMultiService;

    @Autowired
    private StreamMapper streamMapper;


    @AmqpMethod
    public AmqpStreamListResponse findAllStreamByKey(AmqpFindAllStreamByKey request) {

        List<Stream> streams = streamMultiService.findAllByKey(request.getKeys());
        return new AmqpStreamListResponse(streamMapper.toDtoList(streams));
    }


    @AmqpMethod
    public AmqpStreamKeyListResponse findOfflineStreams(AmqpFindOfflineStream request) {

        List<StreamState> stats = streamStateMultiService.getOffline();
        List<StreamKey> keys = stats.stream().map(StreamState::getStreamKey).collect(Collectors.toList());
        return new AmqpStreamKeyListResponse(keys);
    }

    @AmqpMethod
    public AmqpFindFlappingStreamKeyResponse findFlappingStreams(AmqpFindFlappingStream request) {
        Map<StreamKey,Double> periods = streamStateMultiService.getPeriods();
        return new AmqpFindFlappingStreamKeyResponse(periods);
    }

}
