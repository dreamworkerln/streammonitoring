package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.deserializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;

public class StreamKeyDeserializer extends KeyDeserializer {

    @Override
    public StreamKey deserializeKey(String key, DeserializationContext context) {
        //Use the string key here to return a real map key object

        String[] parts = key.split("\\.", 2);
        return new StreamKey(parts[0], parts[1]);
    }
}

