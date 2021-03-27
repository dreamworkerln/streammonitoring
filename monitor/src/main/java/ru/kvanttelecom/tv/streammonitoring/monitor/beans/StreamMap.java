package ru.kvanttelecom.tv.streammonitoring.monitor.beans;

import org.springframework.stereotype.Component;
import ru.dreamworkerln.spring.utils.common.mapwrapper.ConcurrentMapWrapper;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;


/**
 * Общий список всех стримов
 */
@Component
//                                                 <streamName -> Server>
public class StreamMap extends ConcurrentMapWrapper<String, Stream> {}
