package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.downloader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services.server.ServerService;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.parser.MediaServerStreamParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.dreamworkerln.spring.utils.common.StringUtils.*;
import static ru.dreamworkerln.spring.utils.common.StringUtils.throwIfBlank;
import static ru.kvanttelecom.tv.streammonitoring.monitor.configurations.SpringBeanConfigurations.REST_CLIENT_MEDIASERVER;

@Service
@Slf4j
public class MediaserverStreamDownloader implements StreamDownloader {

    @Autowired
    private MonitorProperties props;

    @Autowired
    private ServerService serverService;

    @Autowired
    @Qualifier(REST_CLIENT_MEDIASERVER)
    private RestClient restClient;

    @Autowired
    MediaServerStreamParser streamParser;

    /**
     * Get stream list from Mediaserver
     * @return List<Stream>
     */
    @Override
    public List<Stream> getAll() {

        List<Stream> result = new ArrayList<>();
        ResponseEntity<String> resp = null;
        String body = null;
        try {

            Map<String, Server> servers = serverService.findAll().stream()
                .collect(Collectors.toMap(Server::getDomainName, Function.identity()));

            for (Server server : servers.values()) {

                String url = props.getProtocol() +
                    server.getDomainName() +
                    "/flussonic/api/media";

                log.trace("GET: {}", url);
                resp = restClient.get(url);

                body = resp.hasBody() ? resp.getBody() : null;
                throwIfBlank(body, "Response <Flussonic Mediaserver>: json<cameras> == empty");

                List<Stream> tmp = streamParser.getArray(body, server);
                result.addAll(tmp);
            }
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
     * Get one stream from Mediaserver
     * @return Optional<Stream>
     */
    @Override
    public Optional<Stream> getOne(String hostname, String name) {
        Optional<Stream> result;
        ResponseEntity<String> resp = null;
        String body = null;
        try {

            Optional<Server> oServer = serverService.findByHostname(hostname);
            oServer.orElseThrow(() -> new IllegalArgumentException("Server " + hostname + " not found"));

            String url = props.getProtocol() +
                oServer.get().getDomainName() +
                "/flussonic/api/media/" + name;

            log.trace("GET: {}", url);
            resp = restClient.get(url);

            body = resp.hasBody() ? resp.getBody() : null;
            throwIfBlank(body, "Response <Flussonic Mediaserver>: json<camera> == empty");

            result = streamParser.getOne(body, oServer.get());
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

}
