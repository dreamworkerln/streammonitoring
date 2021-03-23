package ru.kvanttelecom.tv.streammonitoring.tbot.configurations.amqp;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AMQPConfigurationBot {


//    @Bean
//    public MessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
//        DirectMessageListenerContainer container = new DirectMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames("camera.update");
//        container.setMessageListener(listenerAdapter);
//        return container;
//    }
//
//    @Bean
//    MessageListenerAdapter listenerAdapter(CameraEventReceiver receiver, MessageConverter jsonMessageConverter) {
//        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, jsonMessageConverter);
//        adapter.setDefaultListenerMethod("receive");
//        return adapter;
//    }
}
