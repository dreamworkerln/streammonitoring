package ru.kvanttelecom.tv.streammonitoring.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.utils.startuprunner.BaseStartupRunner;

@Component
@Slf4j
public class MonitorStartupRunner extends BaseStartupRunner {

    @Override
    public void run(ApplicationArguments args) {

        super.run(args);

        //log.info("MonitorStartupRunner run");
    }
}
