package ru.kvanttelecom.tv.streammonitoring.utils.configurations.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AMQPConfiguration {

    public static final String CAMERA_RPC_GET_ALL_CAMERAS_MAGIC_CONSTANT = "CAMERA_RPC_GET_ALL_CAMERAS_MAGIC_CONSTANT";

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // Camera Update Events ---------------------------------------------------------

    @Bean
    public Queue cameraUpdateQueue() {
        return new Queue("camera.update");
    }

    @Bean
    public DirectExchange cameraUpdateExchange() {
        return new DirectExchange("exchange.camera.update");
    }

    @Bean
    public Binding bindingCameraUpdate(DirectExchange cameraUpdateExchange, Queue cameraUpdateQueue) {
        return BindingBuilder.bind(cameraUpdateQueue)
            .to(cameraUpdateExchange)
            .with("routing.camera.update");
    }


    // Camera RPC ---------------------------------------------------------


    @Bean
    public Queue cameraRpcQueue() {
        return new Queue("camera.rpc");
    }


    @Bean
    public DirectExchange cameraRpcExchange() {
        return new DirectExchange("exchange.camera.rpc");
    }

    @Bean
    public Binding bindingCameraRpc(DirectExchange cameraRpcExchange,
                           Queue cameraRpcQueue) {
        return BindingBuilder.bind(cameraRpcQueue)
            .to(cameraRpcExchange)
            .with("routing.camera.rpc");
    }

    // ---------------------------------------------------------

/*
    @Bean
    public Queue testRpcQueue() {
        return new Queue("test.rpc");
    }


    @Bean
    public DirectExchange testRpcExchange() {
        return new DirectExchange("exchange.test.rpc");
    }

    @Bean
    public Binding bindingTestRpc(DirectExchange cameraRpcExchange,
                                    Queue cameraRpcQueue) {
        return BindingBuilder.bind(cameraRpcQueue)
            .to(cameraRpcExchange)
            .with("routing.test.rpc");
    }
    */
}
