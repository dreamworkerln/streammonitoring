package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.parser;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class MediaServerStreamParser {

    public List<StreamDto> getArray(String json, String hostname) {

        List<StreamDto> result = new ArrayList<>();

        JSONArray list = new JSONArray(json);
        for (int i = 0; i < list.length(); i++) {

            JSONObject obj = list.getJSONObject(i).getJSONObject("value");
            StreamDto stream = getStream(obj, hostname);
            result.add(stream);
        }
        return result;
    }

    public Optional<StreamDto> getOne(String json, String hostname) {
        JSONObject obj = new JSONObject(json);
        StreamDto stream = getStream(obj.getJSONObject("value"), hostname);
        return Optional.of(stream);
    }


    // --------------------------------------------------------------

    private StreamDto getStream(JSONObject obj, String hostname) {

        String name = obj.getString("name");
        //log.trace("NAME: {}", name);

        JSONObject stats = obj.getJSONObject("stats");
        //log.trace("STATS: {}", stats.toString());

        boolean alive = stats.optBoolean("alive", false);

        //int retryCount = stats.optInt("retry_count", 0);

        JSONObject options = obj.getJSONObject("options");
        //log.trace("OPTIONS: {}", options.toString());

        String title = options.optString("title", null);

        StreamDto result = new StreamDto();
        result.setName(name);
        result.setHostname(hostname);
        result.setTitle(title);
        //result.setComment(comment);
        //result.setPostalAddress(postalAddress);
        //result.setCoordinates(coordinatesString);
        //result.setClient(null);
        result.setAlive(alive);

        return result;
    }
}
