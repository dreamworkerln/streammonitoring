package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.parser;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Address;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Point;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services.server.ServerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.dreamworkerln.spring.utils.common.Utils.throwIfNull;

@Component
@Slf4j
public class WatcherStreamParser {

    public List<Stream> getArray(String json, Map<String, Server> servers) {

        List<Stream> result = new ArrayList<>();
        JSONArray array = new JSONArray(json);
        // iterate over all streams from watcher
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Stream stream = getStream(obj, servers);
            result.add(stream);
        }


        return result;
    }

    public Optional<Stream> getOne(String json, Map<String, Server> servers) {

        Optional<Stream> result = Optional.empty();
        JSONObject obj = new JSONObject(json);
        return Optional.of(getStream(obj, servers));
    }


    // ------------------------------------------------------------------------

    // Get all flussonic media servers from DB
//    private void reloadServers() {
//        servers = serverService.findAll().stream()
//            .collect(Collectors.toMap(Server::getDomainName, Function.identity()));
//
//    }


    private Stream getStream(JSONObject obj, Map<String, Server> servers) {
        String name = obj.getString("name");
        String title = obj.optString("title");
        String comment = obj.optString("comment");
        String coordinatesString = obj.optString("coordinates");
        String postal_address = obj.optString("postal_address");
        String domainName = obj.getJSONObject("stream_status").getString("server");
        boolean alive = obj.getJSONObject("stream_status").getBoolean("alive");

        Server server = servers.get(domainName);

        throwIfNull(server, "Server " + domainName + "not found");

        Stream stream = new Stream(server, name, title);
        stream.setInitialStateAlive(alive);


        String[] arr = coordinatesString.split(" ");
        Point p = null;
        if (arr.length == 2) {
            p = new Point(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
        }
        Address address = new Address(postal_address, p);

        stream.setAddress(address);
        stream.setComment(comment);
        return stream;
    }
}

