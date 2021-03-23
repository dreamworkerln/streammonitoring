package ru.kvanttelecom.tv.streammonitoring.tbot.entities;

import org.springframework.stereotype.Component;
import ru.dreamworkerln.spring.utils.common.mapwrapper.ConcurrentMapWrapper;
import ru.kvanttelecom.tv.streammonitoring.utils.data.Stream;


/**
 * Hold all cameras, received from monitor
 */
@Component
public class StreamMap extends ConcurrentMapWrapper<String, Stream> {}
