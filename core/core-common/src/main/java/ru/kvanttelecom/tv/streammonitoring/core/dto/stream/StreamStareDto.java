package ru.kvanttelecom.tv.streammonitoring.core.dto.stream;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;

import java.util.concurrent.atomic.AtomicLong;

@Data
public class StreamStareDto {

    private final StreamKey streamKey;
    private int level = 0;
    private boolean alive = false;
    private AtomicLong flapCount = new AtomicLong(0);
}
