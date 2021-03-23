package ru.kvanttelecom.tv.streammonitoring.utils.entities;

import org.springframework.stereotype.Component;
import ru.dreamworkerln.spring.utils.common.mapwrapper.ConcurrentMapWrapper;
import ru.kvanttelecom.tv.streammonitoring.utils.data.Stream;

/**
 * Stream status map of all streams from all flussonic media servers
 * <br>Общий список всех стримов
 */
@Component
public class StreamMap extends ConcurrentMapWrapper<String, Stream> {}
