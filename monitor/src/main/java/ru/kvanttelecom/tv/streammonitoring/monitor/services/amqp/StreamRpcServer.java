package ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.StreamService;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;

import java.util.List;

import static ru.kvanttelecom.tv.streammonitoring.utils.configurations.amqp.AMQPConfiguration.STREAM_RPC_GET_ALL_STREAMS_MAGIC_CONSTANT;

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
    private List<Stream> response(List<String> names) {

        List<Stream> result;

        try {

            boolean findAll = names.size() == 1 && names.get(0).equals(STREAM_RPC_GET_ALL_STREAMS_MAGIC_CONSTANT);

            if(findAll) {
                log.trace("RPC REQUEST <FIND STREAMS ALL>");
                result = streamService.findAll();
            }
            else {
                log.trace("RPC REQUEST <FIND STREAMS BY NAME> PARAMS: {}", names);
                result = streamService.findById(names);
            }

            log.trace("RPC <FIND STREAMS> RESPONSE: {}", result);
        }
        catch(Exception rethrow) {
            log.error("StreamRpcServer.response error:", rethrow);
            throw rethrow;
        }
        return result;
    }


}
