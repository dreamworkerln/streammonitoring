package ru.kvanttelecom.tv.streammonitoring.core.services.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;

import java.util.Map;

@Service
@Slf4j
public class ServerService {

    public Map<String, Server> findAll() {
        return null;
    }

}
