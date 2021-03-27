package ru.kvanttelecom.tv.streammonitoring.core.configurations.properties;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class CoreCommonProperties {

    @Value("${client.protocol:http://}")
    @Getter
    private String protocol;

}
