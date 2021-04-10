package ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.AmqpId;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.ArAbstract;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services.stream.StreamService;

import java.util.List;

@Service
@Slf4j
public class StreamRpcServer {

    @Autowired
    StreamService streamService;

    /**
     * find streams by ids
     */
    @RabbitListener(queues = AmqpId.queue.stream.rpc.find)
    private List<Stream> find(ArAbstract request) {

        log.info("AMQP request: {}", request);

        List<Stream> result;

        try {
            log.trace("RPC REQUEST <FIND STREAMS> PARAMS: {}", request);

            result =
            //result = streamService.findAllById(keys);
            log.trace("RPC <FIND STREAMS BY KEY> RESPONSE: {}", "NOT IMPLEMENTED");
        }
        catch(Exception rethrow) {
            log.error("StreamRpcServer.response error:", rethrow);
            throw rethrow;
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
