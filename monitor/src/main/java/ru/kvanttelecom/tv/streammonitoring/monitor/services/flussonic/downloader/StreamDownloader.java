package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.downloader;

import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;

import java.util.List;
import java.util.Optional;

public interface StreamDownloader {
    /**
     * Download all available streams
     */
    List<StreamDto> getAll();

    /**
     * Download stream by name
     */
    Optional<StreamDto> getOne(StreamKey streamKey);
}
