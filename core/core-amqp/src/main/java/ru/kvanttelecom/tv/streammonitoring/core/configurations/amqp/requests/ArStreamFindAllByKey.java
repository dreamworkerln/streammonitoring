package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp.requests;

import lombok.*;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class ArStreamFindAllByKey extends ArAbstract {

    private final List<StreamKey> keys;

    @Override
    public String toString() {
        return "FIND_ALL_STREAMS_BY_KEY";
    }
}
