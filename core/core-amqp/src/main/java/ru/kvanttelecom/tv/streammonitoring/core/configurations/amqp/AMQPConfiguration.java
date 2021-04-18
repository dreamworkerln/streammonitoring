package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class AMQPConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return new Jackson2JsonMessageConverter(objectMapper);
    }


    // STREAM ----------------------------------------------------------------------

    private Queue queueStreamEventUpdate;
    private DirectExchange exchangeStreamEventUpdate;
    private Binding bindingStreamEventUpdate;


    // STREAM RPC ------------------------------

    //  Find
    private Queue queueStreamRpcFind;
    private DirectExchange exchangeStreamRpcFind;
    private Binding bindingStreamRpcFind;



    @PostConstruct
    private void postConstruct() {

        // Stream ----------------------------------------------------------

        // Events -------------------------

        // Update
        queueStreamEventUpdate = new Queue(AmqpId.queue.stream.events.update);
        exchangeStreamEventUpdate = new DirectExchange(AmqpId.exchanger.stream.events.update);
        bindingStreamEventUpdate = BindingBuilder.bind(queueStreamEventUpdate)
            .to(exchangeStreamEventUpdate).with(AmqpId.binding.stream.events.update);

        // RPC -------------------------

        // Find
        queueStreamRpcFind = new Queue(AmqpId.queue.stream.rpc.find);
        exchangeStreamRpcFind = new DirectExchange(AmqpId.exchanger.stream.rpc.find);
        bindingStreamRpcFind = BindingBuilder.bind(queueStreamRpcFind)
            .to(exchangeStreamRpcFind).with(AmqpId.binding.stream.rpc.find);

    }

    // STREAM ----------------------------------------------------------------------

    // Event Update ------------------------

    @Bean(AmqpId.queue.stream.events.update)
    public Queue queueStreamEventUpdate() {
        return queueStreamEventUpdate;
    }

    @Bean(AmqpId.exchanger.stream.events.update)
    public DirectExchange exchangeStreamEventUpdate() {
        return exchangeStreamEventUpdate;
    }

    @Bean(AmqpId.binding.stream.events.update)
    public Binding bindingStreamEventUpdate() {
        return bindingStreamEventUpdate;
    }


//
//    @Bean(AmqpId.queue.stream.events.update)
//    public Queue queueStreamEvent() {
//        return queueStreamEventUpdate;
//    }
//
//    @Bean(AmqpId.exchanger.stream.events.update)
//    public DirectExchange exchangeStreamRpcEvent() {
//        return exchangeStreamEventUpdate;
//    }
//
//    @Bean(AmqpId.binding.stream.events.update)
//    public Binding bindingStreamEvent() {
//        return bindingStreamEventUpdate;
//    }
//
//
    // RPC ---------------------------------

    // FindAll
    @Bean(AmqpId.queue.stream.rpc.find)
    public Queue queueStreamRpcFind() {
        return queueStreamRpcFind;
    }

    @Bean(AmqpId.exchanger.stream.rpc.find)
    public DirectExchange exchangeStreamRpcFind() {
        return exchangeStreamRpcFind;
    }

    @Bean(AmqpId.binding.stream.rpc.find)
    public Binding bindingStreamRpcFind() {
        return bindingStreamRpcFind;
    }
//
//
//    // FindByKeys
//    @Bean(AmqpId.queue.stream.rpc.findByKeys)
//    public Queue queueStreamRpcFindByKeys() {
//        return queueStreamRpcFindByKeys;
//    }
//
//    @Bean(AmqpId.exchanger.stream.rpc.findByKeys)
//    public DirectExchange exchangeStreamRpcFindByKeys() {
//        return exchangeStreamRpcFindByKeys;
//    }
//
//    @Bean(AmqpId.binding.stream.rpc.findByKeys)
//    public Binding bindingStreamRpcFindByKeys() {
//        return bindingStreamRpcFindByKeys;
//    }

    // -------------------------------------------------------------------------------------
}
