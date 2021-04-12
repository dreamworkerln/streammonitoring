package ru.kvanttelecom.tv.streammonitoring.tbot.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.AmqpId;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.ArAbstract;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.ArStreamFindAll;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.ArStreamFindOffline;
import ru.kvanttelecom.tv.streammonitoring.tbot.entities.Stream;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StreamRpcClient {

    private static final String exchanger = AmqpId.exchanger.stream.rpc.find;
    private static final String routing   = AmqpId.binding.stream.rpc.find;

    @Autowired
    private RabbitTemplate template;

    @PostConstruct
    private void postConstruct() {
        //log.trace(bindingStreamRpcFindAll.toString());
    }

//
//    public List<Stream> findAll() {
//
//        List<Stream> result;
//        log.trace("RPC REQUEST <FIND STREAMS ALL>");
//
//        ArAbstract reqFindAll = new ArStreamFindAll();
//
//        ParameterizedTypeReference<List<Stream>> resTypeRef = new ParameterizedTypeReference<>() {};
//        result = template.convertSendAndReceiveAsType(exchanger, routing, reqFindAll, resTypeRef);
//        log.trace("RPC RESPONSE: {}", result);
//
//        if(result == null) {
//            throw new RuntimeException("RPC <FIND STREAMS ALL>: NO RESPONSE");
//        }
//        return result;
//    }



//    public List<Stream> findByKeys(List<Long> ids) {
//        throw new NotImplementedException();
//    }


    public List<Stream> findOffline() {



        List<Stream> result;

        log.trace("RPC REQUEST <FIND OFFLINE STREAMS>");

        String exchanger = AmqpId.exchanger.stream.rpc.find;
        String routing = AmqpId.binding.stream.rpc.find;

        ArStreamFindOffline request = new ArStreamFindOffline();

        ParameterizedTypeReference<List<String>> typeRef = new ParameterizedTypeReference<>() {};

        List<String> tmp = template.convertSendAndReceiveAsType(exchanger, routing, request, typeRef);

        if(tmp == null) {
            throw new RuntimeException("RPC <FIND OFFLINE STREAMS>: NO RESPONSE");
        }

        result = tmp.stream()
            .map(s -> {
                String[] ss = s.split("\\.", 2);
                return new Stream(ss[0], ss[1], false);
            })
            .collect(Collectors.toList());
        log.trace("RPC RESPONSE: {}", result);
        return result;
    }

    // ----------------------------------------------------------------------------

}
