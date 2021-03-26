package ru.kvanttelecom.tv.streammonitoring.core.data;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.kvanttelecom.tv.streammonitoring.core.data.events.mediaserver.MediaServerEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Internal stream status info
 */
@Slf4j
public class StreamState {

    // Порог минимального числа изменений update.alive
    // Для начала вычисления частоты изменения update.alive стрима
    public static final int UPDATE_ALIVE_CHANGE_COUNT_MIN = 10;

    // how many times StreamUpdate.alive was changed since last calculation
    private final AtomicInteger updateAliveChangeCount = new AtomicInteger();

    // время последнего вычисления LevelChangeRps
    private final AtomicReference<Instant> lastUpdateAliveCalcTime = new AtomicReference<>(Instant.now());

    @Getter
    @Setter
    private int level = 0;

    // Значение StreamUpdate.alive при последнем обновлении состояния стрима
    @Getter
    @Setter
    private boolean lastUpdateAlive;

    /**
     * Вычисляет для стрима количество изменений StreamUpdate.alive в секунду
     * <br>Для начала вычисления, необходимо,
     * чтобы StreamUpdate.alive изменилось не менее, чем UPDATE_ALIVE_CHANGE_COUNT_MIN раз.
     * @param update StreamUpdate
     * @return null, если вычисление не производилось, double - результат
     */
    public Double calculateUpdateAliveFreq(MediaServerEvent update) {

        Double result = null;

        // Если update.alive изменилось
        if(lastUpdateAlive != update.isAlive()) {


            log.trace("Stream {} flap count: {}", update.getName(), updateAliveChangeCount.get());

            // увеличиваем aliveChangeCount
            // Производим расчет частоты изменения updateAlive,
            // только если количество изменений updateAlive с предыдущего вычисления calculateLevelFreq больше 10
            if(updateAliveChangeCount.incrementAndGet() >= UPDATE_ALIVE_CHANGE_COUNT_MIN) {
                Instant now = Instant.now();
                Duration d = Duration.between(lastUpdateAliveCalcTime.getAndSet(now), now);
                result =  (double) updateAliveChangeCount.getAndSet(0) / d.toSeconds();
            }

            // обновляем состояние lastUpdateAlive по данным из update
            lastUpdateAlive = update.isAlive();
        }
        return result;
    }



}