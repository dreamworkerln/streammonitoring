package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.downloader;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services.server.ServerService;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.parser.WatcherStreamParser;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


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
    public List<Stream> getAll() {

        List<Stream> result;
        ResponseEntity<String> resp = null;
        String body = null;
        try {
            String url = props.getProtocol() +
                props.getWatcher().getAddress() +
                "/vsaas/api/v2/cameras?limit=" + STREAM_LIMIT;

            log.trace("GET: {}", url);
            resp = restClient.get(url);

            body = resp.hasBody() ? resp.getBody() : null;
            throwIfBlank(body, "Response <Flussonic Watcher>: json<cameras> == empty");

            Map<String, Server> servers = serverService.findAll().stream()
                .collect(Collectors.toMap(Server::getDomainName, Function.identity()));

            result = streamParser.getArray(body, servers);
        }
        // for log append
        catch (Exception rethrow) {
            String message = "Watcher get cameras error:";
            if (resp != null) {
                message = formatMsg(message + " {}, {}", resp.getStatusCode(), body);
            }
            log.error(message, rethrow);
            throw rethrow;
        }
        return result;
    }





    /**
     * Get one stream from Watcher
     * @return Optional<Stream>
     */
    @Override
    public Optional<Stream> getOne(String hostname, String name) {
        Optional<Stream> result;
        ResponseEntity<String> resp = null;
        String body = null;
        try {
            String url = props.getProtocol() +
                props.getWatcher().getAddress() +
                "/vsaas/api/v2/cameras/" + name;

            log.trace("GET: {}", url);
            resp = restClient.get(url);

            body = resp.hasBody() ? resp.getBody() : null;
            throwIfBlank(body, "Response <Flussonic Watcher>: json<camera> == empty");

            Map<String, Server> servers = serverService.findAll().stream()
                .collect(Collectors.toMap(Server::getDomainName, Function.identity()));

            result = streamParser.getOne(body, servers);
        }
        // for log append
        catch (Exception rethrow) {
            String message = "Watcher get cameras error:";
            if (resp != null) {
                message = formatMsg(message + " {}, {}", resp.getStatusCode(), body);
            }
            log.error(message, rethrow);
            throw rethrow;
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

            JSONObject response = new JSONObject(body);
            result = response.getString("session");
        }
        // for log append
        catch (Exception rethrow) {
            String message = "Watcher authentication error:";
            if(resp != null) {
                message = formatMsg(message + " {}, {}", resp.getStatusCode(), body);
            }
            log.error(message, rethrow);
            throw rethrow;
        }
        return result;
    }
}
