package ru.kvanttelecom.tv.streammonitoring.utils.startuprunner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;

import java.util.Arrays;

import static ru.dreamworkerln.spring.utils.common.StringUtils.isBlank;

@Slf4j
public class BaseStartupRunner implements ApplicationRunner {

    @Autowired
    private Environment env;

    @Override
    public void run(ApplicationArguments args) {

        String port = env.getProperty("local.server.port");
        if (!isBlank(port)) {
            log.info("Embedded Tomcat run at port: {}", port);
        }
        log.info("ACTIVE PROFILE: {}", Arrays.toString(env.getActiveProfiles()));
    }
}
