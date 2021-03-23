package ru.kvanttelecom.tv.streammonitoring.tbot.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.utils.data.Stream;

import java.util.ArrayList;
import java.util.List;

import static ru.kvanttelecom.tv.streammonitoring.utils.configurations.amqp.AMQPConfiguration.STREAM_RPC_GET_ALL_STREAMS_MAGIC_CONSTANT;

@Service
@Slf4j
public class StreamRpcClient {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange exchangeStreamRpc;

    @Autowired
    private Binding bindingStreamRpc;

    public List<Stream> findAll() {

        List<Stream> result;
        log.trace("RPC REQUEST <FIND STREAMS ALL>");

        List<String> names = List.of(STREAM_RPC_GET_ALL_STREAMS_MAGIC_CONSTANT);

        result = findRpc(names);

        if(result == null) {
            throw new RuntimeException("RPC <FIND STREAMS ALL>: NO RESPONSE");
        }
        return result;
    }



    public List<Stream> findByName(List<String> names) {

        List<Stream> result;

        if(names.size() == 0) {
            return new ArrayList<>();
        }

        log.trace("RPC REQUEST <FIND STREAMS BY NAME>: {}", names);

        result = findRpc(names);

        if(result == null) {
            throw new RuntimeException("RPC <FIND STREAMS BY NAME>: NO RESPONSE");
        }

        return result;
    }

    // ----------------------------------------------------------------------------


    private List<Stream> findRpc(List<String> names) {
        List<Stream> result;
        String exchanger = exchangeStreamRpc.getName();
        String routing = bindingStreamRpc.getRoutingKey();

        ParameterizedTypeReference<ArrayList<Stream>> typeRef = new ParameterizedTypeReference<>() {};
        result = template.convertSendAndReceiveAsType(exchanger, routing, names, typeRef);
        log.trace("RPC RESPONSE: {}", result);
        return result;
    }

}
