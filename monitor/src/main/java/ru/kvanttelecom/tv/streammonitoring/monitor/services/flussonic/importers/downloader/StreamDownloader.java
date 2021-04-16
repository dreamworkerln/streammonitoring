package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.downloader;

import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;

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
