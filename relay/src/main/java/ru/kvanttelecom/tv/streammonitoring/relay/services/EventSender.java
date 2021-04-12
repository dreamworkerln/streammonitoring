package ru.kvanttelecom.tv.streammonitoring.relay.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.dreamworkerln.spring.utils.common.threadpool.BlockingJobPool;
import ru.dreamworkerln.spring.utils.common.threadpool.JobResult;
import ru.kvanttelecom.tv.streammonitoring.core.configurations.properties.CoreCommonProperties;
import ru.kvanttelecom.tv.streammonitoring.relay.configurations.properties.RelayProperties;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.pivovarit.function.ThrowingFunction.unchecked;

@Service
@Slf4j
public class EventSender {

    @Autowired
    private RelayProperties props;

    @Autowired
    private RestClient restClient;

    @Autowired
    BlockingJobPool<Void,Void> jobPool;




    public void send(String json) {
        List<String> receivers = props.getReceiverList();
        for (String receiver : receivers) {
            jobPool.add(null, unused -> {

                try {
                    String url = props.getProtocol() + receiver;
                    log.trace("POST TO: {}", url);
                    restClient.post(url, json);
                }
                catch (Exception skip) {
                    log.trace("Post error: ", skip); // trace - cause log pollution if error
                }

                return new JobResult<>();
            });
        }
    }
}
/*

    unchecked(unused -> {
    //TimeUnit.SECONDS.sleep(10);
        restClient.post(props.getProtocol() + receiver, json);
        return new JobResult<>();
    })


 */
