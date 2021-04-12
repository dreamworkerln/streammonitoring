package ru.kvanttelecom.tv.streammonitoring.monitor.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.StreamManager;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.stream.StreamStateService;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Main monitoring process, has been scheduled in interval
 */
@Service
@Slf4j
public class MonitoringScheduler {


    @Autowired
    private StreamManager manager;

    @Autowired
    private StreamStateService stateService;

    /**
     * Watcher import scheduler
     */
    //
    @Scheduled(fixedDelayString = "#{monitorProperties.getRefreshIntervalSec() * 1000}",
        initialDelayString = "#{3 * 1000}")
    private void updateStreams() {

        log.trace("Monitor: update streams");
        //log.trace("MONITOR - UPDATE STREAMS ==============================================");
        manager.importAll();
    }





    /**
     * Check duplicate streams (by name) on different flussonic media servers
     * <b>If duplicate stream found on server then all streams update from this server will be rejected
     * @param update update from specific server
     * @param nameIndex streams name index of all streams on all servers
     *//*
    private void checkDuplicate(List<MediaServerEvent> update, Map<String, MediaServerEvent> nameIndex) {
        // check for duplicates

        if(!checkStreamUniq) {
            return;
        }

        List<StreamUpdateDuplicate> duplicates = new ArrayList<>();

        update.forEach(e -> {
            MediaServerEvent exists = nameIndex.get(e.getStreamName());
            if(nameIndex.containsKey(e.getStreamName())) {
                duplicates.add(new StreamUpdateDuplicate(exists, e));
            }
            else {
                nameIndex.put(e.getStreamName(), e);
            }
        });

        if(duplicates.size() > 0) {
            StringBuilder sb = new StringBuilder("Found duplicate streams:\n");

            String prefix = "";
            for (StreamUpdateDuplicate d : duplicates) {
                sb.append(prefix);
                prefix = "\n";
                sb.append(d);

*//*                // FixMe  - удалить строку, поиск сканированием всех строк
                // Добавил, чтобы удалялись дубликаты стримов, иначе весь update будет отброшен
                update.removeIf(u->u.getName().equals(d.exists.getName()));
*//*
            }
            throw new IllegalArgumentException(sb.toString());
        }
    }*/





}
