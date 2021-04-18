package ru.kvanttelecom.tv.streammonitoring.core.dto.stream;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class StreamKeyDto {

    private String hostname;
    private String name;

    @Override
    public String toString() {
        return hostname + "." + name;
    }
}
