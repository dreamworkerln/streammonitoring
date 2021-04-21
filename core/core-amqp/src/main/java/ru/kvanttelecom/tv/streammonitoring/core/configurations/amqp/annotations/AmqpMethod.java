package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.annotations;


import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.AmqpRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AmqpMethod {}

/*
public @interface AmqpMethod {
    Class<? extends AmqpRequest> value();
}
*/