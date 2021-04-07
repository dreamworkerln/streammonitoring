package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Address;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Point;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services.server.ServerService;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.dreamworkerln.spring.utils.common.StringUtils.formatMsg;
import static ru.kvanttelecom.tv.streammonitoring.monitor.configurations.SpringBeanConfigurations.REST_CLIENT_WATCHER;

@Service
@Slf4j
public class WatcherGrabber {

    private String token;

    @Autowired
    @Qualifier(REST_CLIENT_WATCHER)
    private RestClient restClient;

    @Autowired
    private MonitorProperties props;

    @Autowired
    private ServerService serverService;


    @PostConstruct
    private void postConstruct() {}


    public String login() {

        String result = null;

        JSONObject login = new JSONObject();

        String username = props.getWatcher().getUsername();
        String password = props.getWatcher().getPassword();

        login.put("login", username);
        login.put("password", password);
        ResponseEntity<String> resp = null;

        try {
            restClient.post(props.getWatcher().getAddress(), login.toString());

            String body = resp.getBody();
            JSONObject response = new JSONObject(body);
            result = response.getString("session");

        }
        catch (Exception rethrow) {
            String message = "Can't authenticate on watcher:";
            if(resp != null) {
                message = formatMsg(message + " {}, {}", resp.getStatusCode(), resp.getBody());
            }
            log.error(message, rethrow);
            throw rethrow;
        }
        return result;
    }



    public List<Stream> getStreamList() {

        List<Stream> result = new ArrayList<>();
        ResponseEntity<String> resp = null;
        try {

            String camerasUrl = props.getProtocol() +
                props.getWatcher().getAddress() +
                "/vsaas/api/v2/cameras?limit=1000000";

            resp = restClient.get(camerasUrl);

            String body = resp.getBody();
            JSONArray cameras = new JSONArray(body);

            for (int i = 0; i < cameras.length(); i++) {

                JSONObject camera = cameras.getJSONObject(i);

                String name = camera.getString("name");
                String title = camera.optString("title");
                String comment = camera.optString("comment");
                String coordinatesString = camera.optString("coordinates");
                String postal_address = camera.optString("postal_address");
                String domainName = camera.getJSONObject("stream_status").getString("server");
                boolean alive = camera.getJSONObject("stream_status").getBoolean("alive");

                Optional<Server> oServer = serverService.findByDomainName(domainName);
                if (oServer.isEmpty()) {
                    throw new IllegalArgumentException("Server " + domainName + "not found");
                }

                Stream stream = new Stream(oServer.get(), name, title, alive);


                String[] arr = coordinatesString.split(" ");
                Point p = null;
                if (arr.length == 2) {
                    p = new Point(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
                }
                Address address = new Address(postal_address, p);

                stream.setAddress(address);
                stream.setComment(comment);

                result.add(stream);
            }
        }
        catch (Exception rethrow) {
            String message = "Watcher get cameras error:";
            if (resp != null) {
                message = formatMsg(message + " {}, {}", resp.getStatusCode(), resp.getBody());
            }
            log.error(message, rethrow);
            throw rethrow;
        }

        return result;
    }

}
