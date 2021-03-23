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

import static ru.kvanttelecom.tv.streammonitoring.utils.configurations.amqp.AMQPConfiguration.CAMERA_RPC_GET_ALL_CAMERAS_MAGIC_CONSTANT;

@Service
@Slf4j
public class CameraRpcClient {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange cameraRpcExchange;

    @Autowired
    private Binding bindingCameraRpc;

    public List<Stream> findAll() {

        List<Stream> result;
        log.trace("RPC REQUEST <FIND CAMERAS ALL>");

        List<String> names = List.of(CAMERA_RPC_GET_ALL_CAMERAS_MAGIC_CONSTANT);

        result = findRpc(names);

        if(result == null) {
            throw new RuntimeException("RPC <FIND CAMERAS ALL>: NO RESPONSE");
        }
        return result;
    }



    public List<Stream> findByName(List<String> names) {

        List<Stream> result;

        if(names.size() == 0) {
            return new ArrayList<>();
        }

        log.trace("RPC REQUEST <FIND CAMERAS BY NAME>: {}", names);

        result = findRpc(names);

        if(result == null) {
            throw new RuntimeException("RPC <FIND CAMERAS BY NAME>: NO RESPONSE");
        }

        return result;
    }

    // ----------------------------------------------------------------------------


    private List<Stream> findRpc(List<String> names) {
        List<Stream> result;
        String exchanger = cameraRpcExchange.getName();
        String routing = bindingCameraRpc.getRoutingKey();

        ParameterizedTypeReference<ArrayList<Stream>> typeRef = new ParameterizedTypeReference<>() {};
        result = template.convertSendAndReceiveAsType(exchanger, routing, names, typeRef);
        log.trace("RPC RESPONSE: {}", result);
        return result;
    }

}
