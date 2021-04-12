package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.importers.downloader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
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


        Map<String, Server> servers = serverService.findAll().stream()
            .collect(Collectors.toMap(Server::getDomainName, Function.identity()));

        for (Server server : servers.values()) {

            // skip one mediaserver on fail, proceed with others
            try {

                String url = props.getProtocol() +
                    server.getDomainName() +
                    "/flussonic/api/media";

                try {
                    log.trace("GET: {}", url);
                    resp = restClient.get(url);
                    body = resp.hasBody() ? resp.getBody() : null;
                    throwIfBlank(body, "Response <Flussonic Mediaserver>: json<cameras> == empty");
                } catch (Exception rethrow) {
                    throw new RuntimeException("Mediaserver download cameras error:", rethrow);
                }

                try {
                    List<Stream> tmp = streamParser.getArray(body, server);
                    result.addAll(tmp);
                } catch (Exception rethrow) {
                    String message = formatMsg("Mediaserver parse cameras error:" + " {}, {}", resp.getStatusCode(), body);
                    throw new RuntimeException(message, rethrow);
                }

            }
            catch (Exception skip) {
                // log.error -> log.trace : avoid log pollution
                log.trace("Mediaserver {} import error, SKIPPING", server.getHostname(), skip);
            }
        }
        return result;
    }


    // FLUSSONIC MEDIASERVER HTTP API GET ONE NOT WORKING - KLUDGE - GET ALL STREAMS
    /**
     * Get one stream from Mediaserver
     * @return Optional<Stream>
     */
    @Override
    public Optional<Stream> getOne(StreamKey streamKey) {

        Optional<Stream> result;
        ResponseEntity<String> resp;
        String body;

        Optional<Server> oServer = serverService.findByHostname(streamKey.getHostname());
        oServer.orElseThrow(() -> new IllegalArgumentException("Server " + streamKey.getHostname() + " not found"));

        String url = props.getProtocol() +
            oServer.get().getDomainName() +
            "/flussonic/api/media";

        // downloading
        try {
            log.trace("GET: {}", url);
            resp = restClient.get(url);
            body = resp.hasBody() ? resp.getBody() : null;
            throwIfBlank(body, "Response <Flussonic Mediaserver>: json<camera> == empty");
        }
        catch (Exception rethrow) {
            throw new RuntimeException("Mediaserver download camera error:", rethrow);
        }

        // parsing
        try {
            result = Optional.ofNullable(
            streamParser.getArray(body, oServer.get()).stream()
                .collect(Collectors.toMap(Stream::getName, Function.identity()))
                .get(streamKey.getName())
            );
        }
        catch (Exception rethrow) {
            String message = formatMsg("Mediaserver parse camera error:" + " {}, {}", resp.getStatusCode(), body);
            throw new RuntimeException(message, rethrow);
        }
        return result;


    }

// FLUSSONIC MEDIASERVER HTTP API GET ONE NOT WORKING
//
//    /**
//     * Get one stream from Mediaserver
//     * @return Optional<Stream>
//     */
//    @Override
//    public Optional<Stream> getOne(String hostname, String name) {
//        Optional<Stream> result;
//        ResponseEntity<String> resp = null;
//        String body = null;
//
//        Optional<Server> oServer = serverService.findByHostname(hostname);
//        oServer.orElseThrow(() -> new IllegalArgumentException("Server " + hostname + " not found"));
//
//        String url = props.getProtocol() +
//            oServer.get().getDomainName() +
//            "/flussonic/api/media?name=" + name;
//
//        // downloading
//        try {
//            log.trace("GET: {}", url);
//            resp = restClient.get(url);
//            body = resp.hasBody() ? resp.getBody() : null;
//            throwIfBlank(body, "Response <Flussonic Mediaserver>: json<camera> == empty");
//        }
//        catch (Exception rethrow) {
//            throw new IllegalArgumentException("Mediaserver download camera error:", rethrow);
//        }
//
//        // parsing
//        try {
//            result = streamParser.getOne(body, oServer.get());
//        }
//        catch (Exception rethrow) {
//            String message = formatMsg("Mediaserver parse camera error:" + " {}, {}", resp.getStatusCode(), body);
//            throw new IllegalArgumentException(message, rethrow);
//        }
//        return result;
//    }

}
