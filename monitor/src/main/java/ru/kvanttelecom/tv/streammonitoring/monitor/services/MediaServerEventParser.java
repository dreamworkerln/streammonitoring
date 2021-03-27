package ru.kvanttelecom.tv.streammonitoring.monitor.services;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.enums.MediaServerEventType;
import ru.kvanttelecom.tv.streammonitoring.monitor.data.events.mediaserver.MediaServerEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static ru.dreamworkerln.spring.utils.common.StringUtils.formatMsg;
import static ru.dreamworkerln.spring.utils.common.StringUtils.isBlank;


/**
 * Parse events from flussonic media server
 */
@Service
@Slf4j
public class MediaServerEventParser {


    /**
     * Pase event from flussonic media server
     * @param json - event json body
     */
    public List<MediaServerEvent> parseEvent(String json) {


        List<MediaServerEvent> result = new ArrayList<>();

        if(isBlank(json)) {
            return result;
        }

        String streamName = "<null>";
        try {

            JSONArray list = new JSONArray(json);
            //log.trace("list: {}", list);

            for (int i = 0; i < list.length(); i++) {

                // Stream name
                JSONObject event = list.getJSONObject(i);
                streamName = event.getString("media");

                // server host name
                String serverName = event.getString("server");

                // time
                long utcMs = event.getLong("utc_ms");
                Instant time = Instant.ofEpochMilli(utcMs);

                // reason
                String reason = event.optString("reason", null);

                // event type
                MediaServerEventType eventType = MediaServerEventType.UNKNOWN;
                String stringType = event.getString("event");
                try {
                    eventType = MediaServerEventType.valueOf(stringType.toUpperCase());
                }
                catch(IllegalArgumentException rethrow) {
                    String message = formatMsg("PARSING STREAM PROBLEM: {} ", streamName);
                    log.error(message, rethrow);
                    throw rethrow;
                }

                MediaServerEvent mediaServerEvent =
                    new MediaServerEvent(eventType, serverName, streamName, time, reason);

                result.add(mediaServerEvent);
            }
        }
        // try-catch used only to write error message to log, rethrowing
        catch(JSONException rethrow) {
            String message =
                formatMsg("PARSING STREAM PROBLEM: {} ", streamName) +
                    formatMsg("JSON PARSE ERROR: {} ", rethrow.getMessage()) +
                    formatMsg("PROBLEM JSON:\n{}", json);
            log.error(message);
            throw rethrow;
        }
        return result;
    }

}
