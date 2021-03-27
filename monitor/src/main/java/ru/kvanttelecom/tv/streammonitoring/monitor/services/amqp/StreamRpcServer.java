package ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.utils.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.services.StreamService;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StreamRpcServer {

    @Autowired
    StreamService streamService;

    /**
     * find streams by name
     * @param names name of streams, name == null - find all streams
     * @return List<Stream>
     */
    @RabbitListener(queues = "#{@queueStreamRpc.getName()}")
    private Map<StreamKey,Stream> findByKeys(Set<StreamKey> keys) {

        Map<StreamKey,Stream> result;

        try {

            log.trace("RPC REQUEST <FIND STREAMS BY KEY> PARAMS: {}", keys);
            result = streamService.findByKeys(keys);
            log.trace("RPC <FIND STREAMS BY KEY> RESPONSE: {}", result);
        }
        catch(Exception rethrow) {
            log.error("StreamRpcServer.response error:", rethrow);
            throw rethrow;
        }
        return result;
    }


    @RabbitListener(queues = "#{@queueStreamRpc.getName()}")
    private Map<StreamKey,Stream> findAll(Long l) {

        Map<StreamKey,Stream> result;

        try {
            log.trace("RPC REQUEST <FIND STREAMS ALL>");
            result = streamService.findAll(null).stream()
                .collect(Collectors.toMap(Stream::getStreamKey, Function.identity()));
            log.trace("RPC <FIND STREAMS ALL> RESPONSE: {}", result);
        }
        catch(Exception rethrow) {
            log.error("StreamRpcServer.response error:", rethrow);
            throw rethrow;
        }
        return result;
    }


}
