package ru.kvanttelecom.tv.streammonitoring.core.dto.stream;

import lombok.Data;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;

import java.util.Map;

@Data
public class StreamFlappingDto {
    private Map<StreamKey,Long> list;
}
