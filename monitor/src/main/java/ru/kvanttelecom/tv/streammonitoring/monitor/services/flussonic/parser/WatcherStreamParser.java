package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.parser;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Address;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Point;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ru.dreamworkerln.spring.utils.common.Utils.throwIfNull;

@Component
@Slf4j
public class WatcherStreamParser {

    public List<StreamDto> getArray(String json) {

        List<StreamDto> result = new ArrayList<>();
        JSONArray array = new JSONArray(json);
        // iterate over all streams from watcher
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            StreamDto dto = getStream(obj);
            result.add(dto);
        }
        return result;
    }

    public Optional<StreamDto> getOne(String json) {

        JSONObject obj = new JSONObject(json);
        return Optional.of(getStream(obj));
    }


    // ------------------------------------------------------------------------

    // Get all flussonic media servers from DB
//    private void reloadServers() {
//        servers = serverService.findAll().stream()
//            .collect(Collectors.toMap(Server::getDomainName, Function.identity()));
//
//    }


    private StreamDto getStream(JSONObject obj) {
        String name = obj.getString("name");
        String title = obj.optString("title");
        String comment = obj.optString("comment");
        String coordinatesString = obj.optString("coordinates");
        String postalAddress = obj.optString("postal_address");
        String domainName = obj.getJSONObject("stream_status").getString("server");
        String hostname = domainName.split("\\.", 2)[0].toLowerCase();
        boolean alive = obj.getJSONObject("stream_status").optBoolean("alive", false);
        throwIfNull(hostname, "Server " + hostname + "not found");


        StreamDto result = new StreamDto();
        result.setName(name);
        result.setHostname(hostname);
        result.setTitle(title);
        result.setComment(comment);
        result.setPostalAddress(postalAddress);
        result.setCoordinates(coordinatesString);
        result.setClient(null);
        result.setAlive(alive);
        
        return result;
    }
}

/*

    String[] arr = coordinatesString.split(" ");
    Point p = null;
        if (arr.length == 2) {
            p = new Point(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
            }
            Address address = new Address(postal_address, p);
*/
