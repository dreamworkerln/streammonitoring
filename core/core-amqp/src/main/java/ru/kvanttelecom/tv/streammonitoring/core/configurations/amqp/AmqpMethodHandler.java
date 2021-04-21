package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp;

import com.pivovarit.function.ThrowingFunction;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests.AmqpRequest;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.responses.AmqpResponse;

public interface AmqpMethodHandler extends ThrowingFunction<AmqpRequest, AmqpResponse, Exception> {}