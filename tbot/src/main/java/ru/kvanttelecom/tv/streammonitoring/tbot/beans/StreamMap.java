package ru.kvanttelecom.tv.streammonitoring.tbot.beans;

import org.springframework.stereotype.Component;
import ru.dreamworkerln.spring.utils.common.mapwrapper.ConcurrentMapWrapper;
import ru.kvanttelecom.tv.streammonitoring.tbot.entities.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamKey;


/**
 * Общий список всех стримов
 */
@Component
//                                                 <streamName -> Server>
public class StreamMap extends ConcurrentMapWrapper<StreamKey, Stream> {}