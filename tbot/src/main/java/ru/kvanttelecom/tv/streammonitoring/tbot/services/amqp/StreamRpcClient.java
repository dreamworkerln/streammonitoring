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
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.ArStreamFindByKey;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.ArStreamFindOffline;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;

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


    public List<StreamDto> findOffline() {

        List<StreamDto> result;

        log.trace("RPC REQUEST <FIND OFFLINE STREAMS>");

        String exchanger = AmqpId.exchanger.stream.rpc.find;
        String routing = AmqpId.binding.stream.rpc.find;

        ArStreamFindOffline request = new ArStreamFindOffline();

        ParameterizedTypeReference<List<StreamDto>> typeRef = new ParameterizedTypeReference<>() {};

        result = template.convertSendAndReceiveAsType(exchanger, routing, request, typeRef);

        if(result == null) {
            throw new RuntimeException("RPC <FIND OFFLINE STREAMS>: NO RESPONSE");
        }
        log.trace("RPC RESPONSE: {}", result);
        return result;
    }

//    public List<StreamDto> findStreamByKeyList(List<StreamKey> keys) {
//
//        List<StreamDto> result;
//
//        log.trace("RPC REQUEST <FIND STREAMS BY KEY>");
//
//        String exchanger = AmqpId.exchanger.stream.rpc.find;
//        String routing = AmqpId.binding.stream.rpc.find;
//
//        ArStreamFindByKey request = new ArStreamFindByKey(keys);
//        ParameterizedTypeReference<List<StreamDto>> typeRef = new ParameterizedTypeReference<>() {};
//        result = template.convertSendAndReceiveAsType(exchanger, routing, request, typeRef);
//
//        if(result == null) {
//            throw new RuntimeException("RPC <FIND STREAMS BY KEY>: NO RESPONSE");
//        }
//        log.trace("RPC RESPONSE: {}", result);
//        return result;
//    }


    public StreamDto findStreamByKey(StreamKey key) {

        StreamDto result;

        log.trace("RPC REQUEST <FIND STREAMS BY KEY>");

        String exchanger = AmqpId.exchanger.stream.rpc.find;
        String routing = AmqpId.binding.stream.rpc.find;

        ArStreamFindByKey request = new ArStreamFindByKey(key);
        ParameterizedTypeReference<List<StreamDto>> typeRef = new ParameterizedTypeReference<>() {};
        List<StreamDto> tmp = template.convertSendAndReceiveAsType(exchanger, routing, request, typeRef);

        if(tmp == null) {
            throw new RuntimeException("RPC <FIND STREAMS BY KEY>: NO RESPONSE");
        }

        if(tmp.size() == 0) {
            result = null;
        }
        else {
            result = tmp.get(0);
        }

        log.trace("RPC RESPONSE: {}", result);
        return result;
    }


    // ----------------------------------------------------------------------------

}
