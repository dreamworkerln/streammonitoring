package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.parser;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class MediaServerStreamParser {

    public List<Stream> getArray(String json, Server server) {

        List<Stream> result = new ArrayList<>();

        JSONArray list = new JSONArray(json);
        for (int i = 0; i < list.length(); i++) {

            JSONObject obj = list.getJSONObject(i).getJSONObject("value");
            Stream stream = getStream(obj, server);
            result.add(stream);
        }
        return result;
    }

    public Optional<Stream> getOne(String json, Server server) {
        JSONObject obj = new JSONObject(json);
        Stream stream = getStream(obj.getJSONObject("value"), server);
        return Optional.of(stream);
    }


    // --------------------------------------------------------------

    private Stream getStream(JSONObject obj, Server server) {

        Stream result;

        String name = obj.getString("name");
        //log.trace("NAME: {}", name);

        JSONObject stats = obj.getJSONObject("stats");
        //log.trace("STATS: {}", stats.toString());

        boolean alive = stats.optBoolean("alive", false);

        //int retryCount = stats.optInt("retry_count", 0);

        JSONObject options = obj.getJSONObject("options");
        //log.trace("OPTIONS: {}", options.toString());

        String title = options.optString("title", null);

        result = new Stream(server, name, title);
        result.setInitialAliveInternal(alive);

        return result;
    }
}
