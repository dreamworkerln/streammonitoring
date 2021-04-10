package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.downloader;

import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;

import java.util.List;
import java.util.Optional;

public interface StreamDownloader {
    /**
     * Download all available streams
     */
    List<Stream> getAll();

    /**
     * Download stream by name
     */
    Optional<Stream> getOne(String hostname, String name);
}
