package ru.kvanttelecom.tv.streammonitoring.tbot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BotScheduler {

    @Autowired
    StreamSynchronizer synchronizer;

    /**
     * Синхронизирует локальный список стримов с monitor (целиком)
     */
    //@Scheduled(fixedDelayString = "#{applicationProperties.getRefreshIntervalSec() * 1000}", initialDelay = 10 * 1000)
    //@Scheduled(fixedDelay = 5 * 1000, initialDelay = 5 * 1000)
    public void syncAllStreams() {

        try {
            synchronizer.syncAll();
        }
        catch(Exception skip) {
            log.error("Synchronization all error: ", skip);
        }
    }
}
