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

    public static final String STREAM_RPC_GET_ALL_STREAMS_MAGIC_CONSTANT = "STREAM_RPC_GET_ALL_MAGIC_CONSTANT_BFG_V_1";

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // Stream Update Events ---------------------------------------------------------

    @Bean
    public Queue queueStreamEvent() {
        return new Queue("queue.stream.event");
    }

    @Bean
    public DirectExchange exchangeStreamUpdate() {
        return new DirectExchange("exchange.stream.event");
    }

    @Bean
    public Binding bindingStreamUpdate(DirectExchange exchangeStreamUpdate, Queue queueStreamEvent) {
        return BindingBuilder.bind(queueStreamEvent)
            .to(exchangeStreamUpdate)
            .with("routing.stream.event");
    }


    // Stream RPC ---------------------------------------------------------


    @Bean
    public Queue queueStreamRpc() {
        return new Queue("queue.stream.rpc");
    }


    @Bean
    public DirectExchange exchangeStreamRpc() {
        return new DirectExchange("exchange.stream.rpc");
    }

    @Bean
    public Binding bindingStreamRpc(DirectExchange exchangeStreamRpc,
                                    Queue queueStreamRpc) {
        return BindingBuilder.bind(queueStreamRpc)
            .to(exchangeStreamRpc)
            .with("routing.stream.rpc");
    }

    // ---------------------------------------------------------
}
