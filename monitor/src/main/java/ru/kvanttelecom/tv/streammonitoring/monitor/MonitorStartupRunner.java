package ru.kvanttelecom.tv.streammonitoring.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.services.cachingservices.ServerService;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;
import ru.kvanttelecom.tv.streammonitoring.utils.startuprunner.BaseStartupRunner;

@Component
@Slf4j
public class MonitorStartupRunner extends BaseStartupRunner {

    @Autowired
    private ServerService serverService;

    @Autowired
    private MonitorProperties props;

    @Override
    public void run(ApplicationArguments args) {
        super.run(args);
        props.getServers().getServerList().forEach(this::addServer);
    }

    private void addServer(String domainName) {
        String hostname = domainName.split("\\.")[0];
        Server server = new Server(hostname, domainName);
        server = serverService.save(server);
    }
}
