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

    //  FindAll
    private Queue queueStreamRpcFindAll;
    private DirectExchange exchangeStreamRpcFindAll;
    private Binding bindingStreamRpcFindAll;

    //  FindByKeys
    private Queue queueStreamRpcFindByKeys;
    private DirectExchange exchangeStreamRpcFindByKeys;
    private Binding bindingStreamRpcFindByKeys;



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

        // FindAll
        queueStreamRpcFindAll = new Queue(AmqpId.queue.stream.rpc.findAll);
        exchangeStreamRpcFindAll = new DirectExchange(AmqpId.exchanger.stream.rpc.findAll);
        bindingStreamRpcFindAll = BindingBuilder.bind(queueStreamRpcFindAll)
            .to(exchangeStreamRpcFindAll).with(AmqpId.binding.stream.rpc.findAll);

        // FindByKeys
        queueStreamRpcFindByKeys = new Queue(AmqpId.queue.stream.rpc.findByKeys);
        exchangeStreamRpcFindByKeys = new DirectExchange(AmqpId.exchanger.stream.rpc.findByKeys);
        bindingStreamRpcFindByKeys = BindingBuilder.bind(queueStreamRpcFindByKeys)
            .to(exchangeStreamRpcFindByKeys).with(AmqpId.binding.stream.rpc.findByKeys);

    }


    // Stream Update Events ---------------------------------------------------------

    @Bean(AmqpId.queue.stream.events.update)
    public Queue queueStreamEvent() {
        return queueStreamEventUpdate;
    }


    @Bean(AmqpId.exchanger.stream.events.update)
    public DirectExchange exchangeStreamRpcEvent() {
        return exchangeStreamEventUpdate;
    }

    @Bean(AmqpId.binding.stream.events.update)
    public Binding bindingStreamEvent() {
        return bindingStreamEventUpdate;
    }


    // Stream RPC ---------------------------------------------------------



    // FindAll
    @Bean(AmqpId.queue.stream.rpc.findAll)
    public Queue queueStreamRpcFindAll() {
        return queueStreamRpcFindAll;
    }

    @Bean(AmqpId.exchanger.stream.rpc.findAll)
    public DirectExchange exchangeStreamRpcFindAll() {
        return exchangeStreamRpcFindAll;
    }

    @Bean(AmqpId.binding.stream.rpc.findAll)
    public Binding bindingStreamRpcFindAll() {
        return bindingStreamRpcFindAll;
    }

    // FindByKeys   --------------------------------------


    @Bean(AmqpId.queue.stream.rpc.findByKeys)
    public Queue queueStreamRpcFindByKeys() {
        return queueStreamRpcFindByKeys;
    }

    @Bean(AmqpId.exchanger.stream.rpc.findByKeys)
    public DirectExchange exchangeStreamRpcFindByKeys() {
        return exchangeStreamRpcFindByKeys;
    }

    @Bean(AmqpId.binding.stream.rpc.findAll)
    public Binding bindingStreamRpcFindByKeys() {
        return bindingStreamRpcFindByKeys;
    }
    // ---------------------------------------------------------
}
