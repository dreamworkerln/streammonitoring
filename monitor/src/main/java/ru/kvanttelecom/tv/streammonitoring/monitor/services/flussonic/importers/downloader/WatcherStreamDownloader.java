package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.downloader;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;
import ru.kvanttelecom.tv.streammonitoring.core.services.cachingservices.ServerService;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.parser.WatcherStreamParser;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;


import static ru.dreamworkerln.spring.utils.common.StringUtils.*;
import static ru.kvanttelecom.tv.streammonitoring.monitor.configurations.SpringBeanConfigurations.REST_CLIENT_WATCHER;

/**
 *
 */

@Service
@Slf4j
public class WatcherStreamDownloader implements StreamDownloader {

    public static final String STREAM_LIMIT = "1000000";

    @Autowired
    @Qualifier(REST_CLIENT_WATCHER)
    private RestClient restClient;

    @Autowired
    private MonitorProperties props;

    @Autowired
    WatcherStreamParser streamParser;

    @Autowired
    ServerService serverService;



    @PostConstruct
    private void postConstruct() {}


    /**
     * Get stream list from Watcher
     * @return List<Stream>
     */
    @Override
    public List<StreamDto> getAll() {

        List<StreamDto> result;
        ResponseEntity<String> resp = null;
        String body = null;

        String url = props.getProtocol() +
            props.getWatcher().getAddress() +
            "/vsaas/api/v2/cameras?limit=" + STREAM_LIMIT;

        try {
            log.trace("GET: {}", url);
            resp = restClient.get(url);
            body = resp.hasBody() ? resp.getBody() : null;
            throwIfBlank(body, "Response <Flussonic Watcher>: json<cameras> == empty");
        }
        catch (Exception rethrow) {
            throw new RuntimeException("Watcher download cameras error:", rethrow);
        }

        try {
            result = streamParser.getArray(body);
        }
        catch (Exception rethrow) {
            String message = formatMsg("Watcher parse cameras error:" + " {}, {}", resp.getStatusCode(), body);
            throw new RuntimeException(message, rethrow);
        }

        return result;
    }





    /**
     * Get one stream from Watcher
     * @return Optional<Stream>
     */
    @Override
    public Optional<StreamDto> getOne(StreamKey streamKey) {
        Optional<StreamDto> result;
        ResponseEntity<String> resp = null;
        String body = null;

        String url = props.getProtocol() +
            props.getWatcher().getAddress() +
            "/vsaas/api/v2/cameras/" + streamKey.getName();

        try {
            log.trace("GET: {}", url);
            resp = restClient.get(url);

            body = resp.hasBody() ? resp.getBody() : null;
            throwIfBlank(body, "Response <Flussonic Watcher>: json<camera> == empty");
        }
        catch (Exception rethrow) {
            throw new RuntimeException("Watcher download camera error:", rethrow);
        }


        try {
            result = streamParser.getOne(body);
        }
        catch (Exception rethrow) {
            String message = formatMsg("Watcher parse camera error:" + " {}, {}", resp.getStatusCode(), body);
            throw new RuntimeException(message, rethrow);
        }

        return result;
    }








    // -------------------------------------------------------------------------------






    /**
     * WARN NOT USED
     */
    private String login() {

        String result;

        JSONObject login = new JSONObject();

        String username = props.getWatcher().getUsername();
        String password = props.getWatcher().getPassword();

        login.put("login", username);
        login.put("password", password);
        ResponseEntity<String> resp = null;
        String body = null;

        try {

            String loginUrl = props.getWatcher().getAddress();
            log.trace("POST: {}", loginUrl);
            resp = restClient.post(loginUrl, login.toString());

            body = resp.hasBody() ? resp.getBody() : null;
            throwIfBlank(body, "Response <Flussonic Watcher>: json<login> == empty");

            Assert.notNull(body, "body == null");
            JSONObject response = new JSONObject(body);
            result = response.getString("session");
        }
        catch (Exception rethrow) {
            String message = "Watcher authentication error: ";
            if(resp != null) {
                message += formatMsg("Watcher authentication error:" + " {}, {}", resp.getStatusCode(), body);
            }
            throw new RuntimeException(message, rethrow);
        }
        return result;
    }
}
