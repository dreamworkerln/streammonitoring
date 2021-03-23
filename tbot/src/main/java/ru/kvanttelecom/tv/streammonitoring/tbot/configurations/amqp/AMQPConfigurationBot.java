package ru.kvanttelecom.tv.streammonitoring.tbot.configurations.amqp;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AMQPConfigurationBot {

    // Manual setup amqp listening handlers

//    @Bean
//    public MessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
//        DirectMessageListenerContainer container = new DirectMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames("stream.update");
//        container.setMessageListener(listenerAdapter);
//        return container;
//    }
//
//    @Bean
//    MessageListenerAdapter listenerAdapter(StreamEventReceiver receiver, MessageConverter jsonMessageConverter) {
//        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, jsonMessageConverter);
//        adapter.setDefaultListenerMethod("receive");
//        return adapter;
//    }
}
