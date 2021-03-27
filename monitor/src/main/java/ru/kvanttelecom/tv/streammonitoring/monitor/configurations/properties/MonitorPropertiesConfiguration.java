package ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Загружатель Properties для различных конфигураций.
 * Префиксы параметров внутри .properties для разных модулей делать разными иначе перекроются.
 * RUN Configurations -> VM Params:
 * Выбрать активный профиль: -Dspring.profiles.active=dev
 * Указать значение (параметром либо переменной окружения SPRING_APPLICATION_JSON):
 * -Dzalupa=очко
 * SPRING_APPLICATION_JSON='{"restreamer.somevalue":"something"}' java -jar restreamer.jar
 *
 * Externalising Spring Boot properties when deploying to Docker
 * https://stackoverflow.com/questions/46057625/externalising-spring-boot-properties-when-deploying-to-docker
 * https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config
 */
@Configuration
public class MonitorPropertiesConfiguration {


    @Configuration
    @Profile("default")
    @PropertySources({
        @PropertySource(value = "classpath:monitor.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:monitor.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:config/monitor.properties", ignoreResourceNotFound = true),
    })
    static class DefaultProperties {}


    @Configuration
    @Profile("!default")
    @PropertySources({
        @PropertySource(value = "classpath:monitor-${spring.profiles.active}.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:monitor-${spring.profiles.active}.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:config/monitor-${spring.profiles.active}", ignoreResourceNotFound = true),
    })
    static class NonDefaultProperties {}
}