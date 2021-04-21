package ru.kvanttelecom.tv.streammonitoring.tbot.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.AmqpId;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.*;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses.AmqpFindFlappingStreamKeyResponse;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses.AmqpStreamKeyListResponse;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses.AmqpStreamListResponse;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;

import java.util.*;

@Service
@Slf4j
public class StreamRpcClient {

    private static final String exchanger = AmqpId.exchanger.stream.rpc.find;
    private static final String routing   = AmqpId.binding.stream.rpc.find;

    @Autowired
    private RabbitTemplate template;

//    @PostConstruct
//    private void postConstruct() {
//        //log.trace(bindingStreamRpcFindAll.toString());
//    }

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



//    public StreamDto findStreamByKey(StreamKey key) {
//
//        StreamDto result;
//
//        log.trace("RPC REQUEST <FIND STREAMS BY KEY>");
//
//        String exchanger = AmqpId.exchanger.stream.rpc.find;
//        String routing = AmqpId.binding.stream.rpc.find;
//
//        AmqpStreamFindByKeyRequest request = new AmqpStreamFindByKeyRequest(key); // List<StreamDto>
//        ParameterizedTypeReference<AmqpStreamFindOneResponse> typeRef = new ParameterizedTypeReference<>() {};
//        AmqpStreamFindOneResponse response = template.convertSendAndReceiveAsType(exchanger, routing, request, typeRef);
//
//        if(response == null) {
//            throw new RuntimeException("RPC <FIND STREAMS BY KEY>: NO RESPONSE");
//        }
//        if(response.getList().size() == 0) {
//            result = null;
//        }
//        else {
//            result = response.getList().get(0);
//        }
//
//        log.trace("RPC RESPONSE: {}", result);
//        return result;
//    }


    /**
     * Find Streams by List<StreamKey>
     * @param keys List<StreamKey>
     * @return List<StreamDto>
     */
    public List<StreamDto> findStreamByKeyList(Iterable<StreamKey> keys) {

        List<StreamDto> result;

        log.trace("RPC REQUEST <FIND STREAMS BY KEY>");
        AmqpRequest request = new AmqpFindAllStreamByKey(keys);
        ParameterizedTypeReference<AmqpStreamListResponse> responseTypeRef = new ParameterizedTypeReference<>() {};
        var response = template.convertSendAndReceiveAsType(exchanger, routing, request, responseTypeRef);
        if(response == null) {
            throw new RuntimeException("RPC <FIND STREAMS BY KEY>: NO RESPONSE");
        }
        result = response.getList();
        log.trace("RPC RESPONSE: {}", result);
        return result;
    }




    /**
     * Find offline Streams
     * @return List<StreamKey>
     */
    public List<StreamKey> findOffline() {

        List<StreamKey> result;

        log.trace("RPC REQUEST <FIND OFFLINE STREAMKEYS>");

        var request = new AmqpFindOfflineStream();

        ParameterizedTypeReference<AmqpStreamKeyListResponse> responseTypeRef = new ParameterizedTypeReference<>() {};

        var response = template.convertSendAndReceiveAsType(exchanger, routing, request, responseTypeRef);
        if(response == null) {
            throw new RuntimeException("RPC <FIND OFFLINE STREAMKEYS>: NO RESPONSE");
        }
        result = response.getList();
        log.trace("RPC RESPONSE: {}", result);
        return result;
    }

    /**
     * Find flapping streams ratio
     * @return List<StreamKey>
     */
    public Map<StreamKey,Double> findFlappingStreams() {

        Map<StreamKey,Double> result;

        log.trace("RPC REQUEST <FIND FLAPPING STREAMS>");

        AmqpRequest request = new AmqpFindFlappingStream();
        ParameterizedTypeReference<AmqpFindFlappingStreamKeyResponse> responseTypeRef = new ParameterizedTypeReference<>() {};
        var response = template.convertSendAndReceiveAsType(exchanger, routing, request, responseTypeRef);

        if(response == null) {
            throw new RuntimeException("RPC <FIND FLAPPING STREAMS>: NO RESPONSE");
        }
        result = response.getMap();

       log.trace("RPC RESPONSE: {}", result);
        return result;
    }

    // ----------------------------------------------------------------------------

}
