package ru.kvanttelecom.tv.streammonitoring.monitor.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.amqp.StreamEventSender;
import ru.kvanttelecom.tv.streammonitoring.utils.entities.StreamMap;
import ru.kvanttelecom.tv.streammonitoring.monitor.entities.ServerMap;
import ru.kvanttelecom.tv.streammonitoring.utils.data.StreamUpdate;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.StreamEventDto;

import javax.annotation.PostConstruct;
import java.util.*;


/**
 * Main monitoring process, has been scheduled in interval
 */
@Service
@Slf4j
public class MonitoringScheduler {

    private boolean checkStreamUniq;

    @Autowired
    private StreamGrabber apiClient;

    @Autowired
    private ServerMap servers;

    @Autowired
    private StreamService streamService;

    @Autowired
    private StreamEventSender streamEventSender;

    @Autowired
    private MonitorProperties props;


    @PostConstruct
    private void postConstruct() {
        checkStreamUniq = props.isCheckStreamUniq();
    }



    /**
     * Monitoring stream updates on flussonic media servers
     */
    //
    @Scheduled(fixedDelayString = "#{monitorProperties.getRefreshIntervalSec() * 1000}",
        initialDelayString = "#{monitorProperties.getRefreshIntervalSec()/2 * 1000}")
    private void monitor() {

        try {

            // Events from all servers
            List<StreamEventDto> events = new ArrayList<>();

            // StreamUpdate name index - checking stream uniqueness (on all servers)
            Map<String, StreamUpdate> nameIndex = new HashMap<>();

            // обходим по всем серверам
            for (Map.Entry<String, StreamMap> server : servers.entrySet()) {

                String serverName = null;
                try {
                    serverName = server.getKey();

                    //log.trace("Scanning server: {}", serverName);

                    // get updates from specified flussonic media server
                    List<StreamUpdate> update = apiClient.getStreamsUpdate(serverName);


                    checkDuplicate(update, nameIndex);

/*                    // FixMe - удалить try catch
                    try {
                        checkDuplicate(update, nameIndex);
                    }
                    catch (Exception skip) {
                        log.error("Found duplicate streams on server: {}", serverName, skip);
                    }
*/


                    // calculate update events for selected server
                    List<StreamEventDto> serverEvents = streamService.applyUpdate(serverName, update);

                    events.addAll(serverEvents);
                }
                // have error - continue to next server
                catch (Exception skip) {
                    log.error("Getting data from server {} error:", serverName, skip);
                }
            }

            // sending to bot ---------------------------------------
            if (events.size() > 0) {
                // sending events to message aggregator(bot)
                try {
                    streamEventSender.send(events);
                }
                // skip on error
                catch (Exception skip) {
                    log.error("Sending to aggregator(bot) error:", skip);
                }
            } else {
                //log.trace("Nothing to send");
            }

        }
        // Unexpected error - shutdown program
        catch (Exception fatal) {
            log.error("Monitor strange error, shutdown: ", fatal);
            System.exit(-1);
        }
    }


    /**
     * Check duplicate streams (by name) on different flussonic media servers
     * <b>If duplicate stream found on server then all streams update from this server will be rejected
     * @param update update from specific server
     * @param nameIndex streams name index of all streams on all servers
     */
    private void checkDuplicate(List<StreamUpdate> update, Map<String, StreamUpdate> nameIndex) {
        // check for duplicates

        if(!checkStreamUniq) {
            return;
        }

        List<StreamUpdateDuplicate> duplicates = new ArrayList<>();

        update.forEach(u -> {
            StreamUpdate exists = nameIndex.get(u.getName());
            if(nameIndex.containsKey(u.getName())) {
                duplicates.add(new StreamUpdateDuplicate(exists, u));
            }
            else {
                nameIndex.put(u.getName(), u);
            }
        });

        if(duplicates.size() > 0) {
            StringBuilder sb = new StringBuilder("Found duplicate streams:\n");

            String prefix = "";
            for (StreamUpdateDuplicate d : duplicates) {
                sb.append(prefix);
                prefix = "\n";
                sb.append(d);

/*                // FixMe  - удалить строку, поиск сканированием всех строк
                // Добавил, чтобы удалялись дубликаты стримов, иначе весь update будет отброшен
                update.removeIf(u->u.getName().equals(d.exists.getName()));
*/
            }
            throw new IllegalArgumentException(sb.toString());
        }
    }



    private static class StreamUpdateDuplicate {

        @Getter
        private final StreamUpdate exists;
        private final StreamUpdate update;


        private StreamUpdateDuplicate(StreamUpdate exists, StreamUpdate update) {
            this.exists = exists;
            this.update = update;
        }

        @Override
        public String toString() {
            return "[" +
                "exists={name='" + exists.getName() + "', label='" + exists.getTitle() + "', server=" + exists.getServerName() + "}, " +
                "update={name='" + update.getName() + "', label='" + update.getTitle() + "', server=" + update.getServerName() + "}]";
        }
    }


}
