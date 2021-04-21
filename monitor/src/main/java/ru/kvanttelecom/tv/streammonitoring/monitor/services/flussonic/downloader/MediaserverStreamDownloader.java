package ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.downloader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.dreamworkerln.spring.utils.common.threadpool.BatchItem;
import ru.dreamworkerln.spring.utils.common.threadpool.BlockingJobPool;
import ru.dreamworkerln.spring.utils.common.threadpool.JobResult;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.services.caching.ServerMultiService;
import ru.kvanttelecom.tv.streammonitoring.monitor.configurations.properties.MonitorProperties;
import ru.kvanttelecom.tv.streammonitoring.monitor.services.flussonic.parser.MediaServerStreamParser;

import java.time.Duration;
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

    private final BlockingJobPool<Void, List<StreamDto>> jobPool =
        new BlockingJobPool<>(5, Duration.ofSeconds(5), null);

    @Autowired
    private MonitorProperties props;

    @Autowired
    private ServerMultiService serverMultiService;

    @Autowired
    @Qualifier(REST_CLIENT_MEDIASERVER)
    private RestClient restClient;

    @Autowired
    MediaServerStreamParser streamParser;

    /**
     * Get stream list from Mediaserver
     * @return List<StreamDto>
     */
    @Override
    public List<StreamDto> getAll() {

        List<StreamDto> result = new ArrayList<>();

        Map<String, Server> servers = serverMultiService.findAll().stream()
            .collect(Collectors.toMap(Server::getDomainName, Function.identity()));


        List<BatchItem<Void, List<StreamDto>>> tasks = new ArrayList<>();
        for (Server server : servers.values()) {

            BatchItem<Void, List<StreamDto>> bitem = new BatchItem<>(null, unused -> {

                String url = props.getProtocol() +
                    server.getDomainName() +
                    "/flussonic/api/media";

                //log.trace("GET: {}", url);
                ResponseEntity<String> resp = restClient.get(url);
                String body = resp.hasBody() ? resp.getBody() : null;
                throwIfBlank(body, "Response <Flussonic Mediaserver>: json<cameras> == empty");

                List<StreamDto> res = null;
                try {
                    res = streamParser.getArray(body, server.getHostname());
                } catch (Exception rethrow) {
                    String message = formatMsg("Mediaserver parse cameras error:" + " {}, {}",
                        resp.getStatusCode(), body);
                    throw new RuntimeException(message, rethrow);
                }
                return new JobResult<>(null, res);
            });

            tasks.add(bitem);
        }


        List<JobResult<Void, List<StreamDto>>> listListDto;
        try {
            listListDto = jobPool.batchBlocking(tasks);
        }
        catch(Exception rethrow) {
            throw new RuntimeException("Something terrible happened", rethrow);
        }


        for (JobResult<Void, List<StreamDto>> jobResult : listListDto) {
            if (jobResult.getException() == null) {
                result.addAll(jobResult.getResult());
            }
        }

        return result;
    }















//        for (Server server : servers.values()) {
//
//            // skip one mediaserver on fail, proceed with others
//            try {
//
//                String url = props.getProtocol() +
//                    server.getDomainName() +
//                    "/flussonic/api/media";
//
//                jobPool.batchAsync();
//                try {
//                    log.trace("GET: {}", url);
//                    resp = restClient.get(url);
//                    body = resp.hasBody() ? resp.getBody() : null;
//                    throwIfBlank(body, "Response <Flussonic Mediaserver>: json<cameras> == empty");
//                } catch (Exception rethrow) {
//                    throw new RuntimeException("Mediaserver download cameras error:", rethrow);
//                }
//
//                try {
//                    List<StreamDto> tmp = streamParser.getArray(body, server.getHostname());
//                    result.addAll(tmp);
//                } catch (Exception rethrow) {
//                    String message = formatMsg("Mediaserver parse cameras error:" + " {}, {}", resp.getStatusCode(), body);
//                    throw new RuntimeException(message, rethrow);
//                }
//
//            }
//            catch (Exception skip) {
//                // log.error -> log.trace : avoid log pollution
//                log.trace("Mediaserver {} import error, SKIPPING", server.getHostname(), skip);
//            }
//        }





    // FLUSSONIC MEDIASERVER HTTP API GET ONE NOT WORKING - KLUDGE - GET ALL STREAMS
    /**
     * Get one stream from Mediaserver
     * @return Optional<Stream>
     */
    @Override
    public Optional<StreamDto> getOne(StreamKey streamKey) {

        Optional<StreamDto> result;
        ResponseEntity<String> resp;
        String body;

        Optional<Server> oServer = serverMultiService.findByHostname(streamKey.getHostname());
        oServer.orElseThrow(() -> new IllegalArgumentException("Server " + streamKey.getHostname() + " not found"));
        Server server = oServer.get();

        String url = props.getProtocol() +
            server.getDomainName() +
            "/flussonic/api/media";

        // downloading
        try {
            //log.trace("GET: {}", url);
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
            streamParser.getArray(body, server.getHostname()).stream()
                .collect(Collectors.toMap(StreamDto::getName, Function.identity()))
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
