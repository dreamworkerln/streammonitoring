package ru.kvanttelecom.tv.streammonitoring.core.repositories.keygen;

import lombok.Data;
import ru.kvanttelecom.tv.streammonitoring.core.cache.NaturalKey;

@Data
public class StreamNaturalKey extends NaturalKey {
    private String hostName;
    private String name;
}
