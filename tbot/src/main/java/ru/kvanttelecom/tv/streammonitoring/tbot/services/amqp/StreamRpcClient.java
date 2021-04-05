package ru.kvanttelecom.tv.streammonitoring.tbot.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.AmqpId;
import ru.kvanttelecom.tv.streammonitoring.tbot.entities.Stream;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
public class StreamRpcClient {

    @Autowired
    private RabbitTemplate template;

    @PostConstruct
    private void postConstruct() {
        //log.trace(bindingStreamRpcFindAll.toString());
    }

    public List<Stream> findAll() {

        List<Stream> result;
        log.trace("RPC REQUEST <FIND STREAMS ALL>");

        String exchanger = AmqpId.exchanger.stream.rpc.findAll;
        String routing   = AmqpId.binding.stream.rpc.findAll;

        ParameterizedTypeReference<List<Stream>> typeRef = new ParameterizedTypeReference<>() {};
        result = template.convertSendAndReceiveAsType(exchanger, routing, new HashSet<>(), typeRef);
        log.trace("RPC RESPONSE: {}", result);

        if(result == null) {
            throw new RuntimeException("RPC <FIND STREAMS ALL>: NO RESPONSE");
        }
        return result;
    }



    public List<Stream> findByKeys(List<Long> ids) {

        List<Stream> result;

        if(ids.size() == 0) {
            return new ArrayList<>();
        }

        log.trace("RPC REQUEST <FIND STREAMS BY NAME>: {}", ids);

        String exchanger = AmqpId.exchanger.stream.rpc.findByKeys;
        String routing = AmqpId.binding.stream.rpc.findByKeys;

        ParameterizedTypeReference<List<Stream>> typeRef = new ParameterizedTypeReference<>() {};
        result = template.convertSendAndReceiveAsType(exchanger, routing, ids, typeRef);
        log.trace("RPC RESPONSE: {}", result);

        if(result == null) {
            throw new RuntimeException("RPC <FIND STREAMS BY NAME>: NO RESPONSE");
        }

        return result;
    }

    // ----------------------------------------------------------------------------




}
