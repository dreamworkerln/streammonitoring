package ru.kvanttelecom.tv.streammonitoring.relay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.utils.startuprunner.BaseStartupRunner;

@Component
@Slf4j
public class RelayRunner extends BaseStartupRunner {

    @Override
    public void run(ApplicationArguments args) {
        super.run(args);

        //log.info("TRACE ENABLED {}", log.isTraceEnabled());
        //log.info("DEBUG ENABLED {}", log.isDebugEnabled());
    }
}
