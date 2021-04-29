package ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.AmqpId;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.AmqpMethodHandler;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.annotations.AmqpController;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.annotations.AmqpDispatcher;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.annotations.AmqpMethod;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.AmqpRequest;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses.AmqpErrorResponse;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses.AmqpResponse;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@AmqpDispatcher
@Slf4j
public class StreamRpcServer {

    Marker marker = MarkerFactory.getMarker("AMQP_MARKER");


    //     <RequestClass, handler>
    private final Map<String, AmqpMethodHandler> handlers = new HashMap<>();

    @Autowired
    ApplicationContext context;

    @PostConstruct
    private void init() {}



    /**
     * Amqp request dispatcher
     */
    @RabbitListener(queues = AmqpId.queue.stream.rpc.find, id = "find", autoStartup = "false")
    @SneakyThrows
    private AmqpResponse find(AmqpRequest request) {

        AmqpResponse result;
        log.trace(marker, "AMQP request: {}", request);

        String methodName = request.getClass().getSimpleName();
        if(handlers.containsKey(methodName)) {
            result = handlers.get(methodName).apply(request);
        }
        else {
            throw new IllegalArgumentException("Method " + methodName + " not found");
        }
        log.trace(marker, "AMQP response: {}", result);
        return result;
    }


    // ================================================================================


    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        initHandlers();
    }


    private void initHandlers() {

        try {

            // Ask spring to find (and load if not loaded?) all beans annotated with @AmqpController
            Map<String,Object> beans = context.getBeansWithAnnotation(AmqpController.class);

            // https://stackoverflow.com/questions/27929965/find-method-level-custom-annotation-in-a-spring-context
            for (Map.Entry<String, Object> entry : beans.entrySet()) {

                Object bean = entry.getValue();
                Class<?> beanClass = bean.getClass();
                AmqpController controller = beanClass.getAnnotation(AmqpController.class);

                // bean is an AOP proxy
                if (controller == null) {
                    beanClass = AopProxyUtils.ultimateTargetClass(bean);
                    controller = beanClass.getAnnotation(AmqpController.class);
                }

                // Ищем в бине метод, помеченный аннотацией @AmqpMethod
                for (Method method : beanClass.getDeclaredMethods()) {

                    if (method.isAnnotationPresent(AmqpMethod.class)) {
                        //Should give you expected results
                        AmqpMethod amqpMethod = method.getAnnotation(AmqpMethod.class);

                        // Checked(как и unchecked) исключения будут проброшены к вызывающему
                        //JrpcMethodHandler handler = params -> (JsonNode)method.invoke(bean, params);
                        AmqpMethodHandler handler = request -> (AmqpResponse)method.invoke(bean, request);

                        // Get method name from method argument class type
                        String controllerMethodName = getArgClass(method).getSimpleName();

                        //String controllerMethodName = jrpcController.value() + "." + jrpcMethod.value();

                        // check that name is unique
                        if(handlers.containsKey(controllerMethodName)) {
                            throw new IllegalArgumentException("amqpController.amqpMethod: " +
                                controllerMethodName + " already exists (duplicate amqp method name)");
                        }
                        handlers.put(controllerMethodName, handler);

                    }
                }
            }

        } catch (BeansException e) {
            throw new RuntimeException(e);
        }

    }

    private Class<?> getArgClass(Method method) {

        Parameter[] parameters = method.getParameters();
        if(parameters.length != 1) {
            throw new IllegalStateException("AmqpMethod " + method.getClass().getSimpleName() + "." + method.getName() +
                "Wrong params count, should be one");
        }
        return parameters[0].getType();
    }


}




//
//
//            //log.trace("RPC REQUEST <FIND STREAMS> PARAMS: {}", request);
//
//            if (request instanceof AmqpFindOfflineStream) {
//
//                List<StreamState> stats = streamStateMultiService.getOffline();
//                List<StreamKey> keys = stats.stream().map(StreamState::getStreamKey).collect(Collectors.toList());
//                List<Stream> offline = streamMultiService.findAllByKey(keys);
//                result = new AmqpStreamFindOfflineResponse(streamMapper.toDtoList(offline));
//
//
//                //log.trace("RPC <FIND STREAMS> RESPONSE: {}", result);
//            }
//            else if(request instanceof AmqpFindStreamByKey) {
//
//                StreamKey key = ((AmqpFindStreamByKey) request).getKey();
//                List<Stream> list = List.of(streamMultiService.findByKey(key).get());
//                result = new AmqpStreamFindOneResponse(streamMapper.toDtoList(list));
//
//                //log.trace("RPC <FIND STREAMS> RESPONSE: {}", result);
//            }
//            else if(request instanceof AmqpFindFlappingStream) {
//
//                Map<StreamKey,Long> flapping = streamStateMultiService.getFlapCounts();
//                result = new AmqpFindFlappingStreamKeyResponse(flapping);
//                //log.trace("RPC <FIND STREAMS> RESPONSE: {}", result);
//            }
//        }
//        catch(Exception rethrow) {
//            throw new RuntimeException("StreamRpcServer.find error:", rethrow);
//        }
//        return result;





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
