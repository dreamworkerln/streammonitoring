package ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.AmqpId;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.ArAbstract;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.ArStreamFindOffline;
import ru.kvanttelecom.tv.streammonitoring.core.services.stream.StreamService;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.stream.StreamStateService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StreamRpcServer {

    @Autowired
    StreamService streamService;

    @Autowired
    StreamStateService streamStateService;

    /**
     * find streams by ids
     */
    @RabbitListener(queues = AmqpId.queue.stream.rpc.find)
    private List<String> find(ArAbstract request) {

        List<String> result = null;
        log.trace("AMQP request: {}", request);


        try {
            log.trace("RPC REQUEST <FIND STREAMS> PARAMS: {}", request);

            if (request instanceof ArStreamFindOffline) {

                result = streamStateService.getOffline().stream()
                    .map(s -> s.getStreamKey().toString())
                    .collect(Collectors.toList());
                
                log.trace("RPC <FIND STREAMS> RESPONSE: {}", result);
            }
        }
        catch(Exception rethrow) {
            throw new RuntimeException("StreamRpcServer.find error:", rethrow);
        }
        return result;
    }





}

// @RabbitListener(queues = "#{@queueStreamRpcGetAll.getName()}")

/*


    @RabbitListener(queues = AmqpId.queue.stream.rpc.findAll)
    private List<Stream> findAll() {

        List<Stream> result;

        try {
            log.trace("RPC REQUEST <FIND STREAMS ALL>");
            result = streamService.findAll();
            log.trace("RPC <FIND STREAMS ALL> RESPONSE: {}", result);
        }
        catch(Exception rethrow) {
            log.error("StreamRpcServer.response error:", rethrow);
            throw rethrow;
        }
        return result;
    }



 */
