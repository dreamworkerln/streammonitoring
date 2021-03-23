package ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.CameraService;
import ru.kvanttelecom.tv.streammonitoring.utils.data.Stream;

import java.util.List;

import static ru.kvanttelecom.tv.streammonitoring.utils.configurations.amqp.AMQPConfiguration.CAMERA_RPC_GET_ALL_CAMERAS_MAGIC_CONSTANT;

@Service
@Slf4j
public class CameraRpcServer {

    @Autowired
    CameraService cameraService;

    /**
     * find cameras by name
     * @param names name of cameras, name == null - find all cameras
     * @return List<Camera>
     */
    @RabbitListener(queues = "#{cameraRpcQueue.getName()}")
    private List<Stream> response(List<String> names) {

        List<Stream> result;

        try {

            boolean findAll = names.size() == 1 && names.get(0).equals(CAMERA_RPC_GET_ALL_CAMERAS_MAGIC_CONSTANT);

            if(findAll) {
                log.trace("RPC REQUEST <FIND CAMERAS ALL>");
                result = cameraService.findAll();
            }
            else {
                log.trace("RPC REQUEST <FIND CAMERAS BY NAME> PARAMS: {}", names);
                result = cameraService.findById(names);
            }

            log.trace("RPC <FIND CAMERAS> RESPONSE: {}", result);
        }
        catch(Exception rethrow) {
            log.error("CameraRpcServer.response error:", rethrow);
            throw rethrow;
        }
        return result;
    }


}
