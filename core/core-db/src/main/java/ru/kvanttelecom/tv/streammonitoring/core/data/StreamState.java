package ru.kvanttelecom.tv.streammonitoring.core.data;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;
import ru.kvanttelecom.tv.streammonitoring.core.services.caching.StreamStateMultiService;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Internal stream status info
 */
@Slf4j
public class StreamState extends AbstractEntity {

    private static final int STREAM_MAX_LEVEL = 10;
    private static final int STREAM_THRESHOLD_LEVEL = (int)(STREAM_MAX_LEVEL * 0.7);

    // минимальный порог фильтрации частоты изменения alive
    // при значениях частоты, ниже указанного стрим не будет отображен как флапающий
    public static final double STREAM_FLAPPING_MIN_RATE = 1e-3; // 1 раз в 1000 сек

    // Порог минимального числа изменений alive
    // для начала вычисления частоты изменения alive стрима
    private static final int UPDATE_ALIVE_CHANGE_COUNT_MIN = 10;


    @Getter
    private final StreamKey streamKey;

    @Getter
    private boolean alive = false;

    //@Getter
    //private boolean flapping = false;

    @Getter
    private double flapRateMoving = 0;

    private final DescriptiveStatistics rateStatistic = new DescriptiveStatistics();

    // ---------------------------------------------------------------------------------------------

    // уровень для гистерезиса
    private int level = 0;

    // Количество изменений alive с момента предыдущего расчета частоты
    // how many times StreamUpdate.alive was changed since last calculation
    private final AtomicInteger aliveChangeCount = new AtomicInteger();

    // время последнего вычисления частоты
    private final AtomicReference<Instant> lastCalculateRateTime = new AtomicReference<>(Instant.now());


    public StreamState(StreamKey streamKey, boolean enabled, boolean alive) {
        this.streamKey = streamKey;
        this.enabled = enabled;
        this.alive = alive;
        rateStatistic.setWindowSize(10);
    }

    public void update(boolean newAlive) {
        alive = newAlive;

        // calculate flapping frequency
        if(alive && !StreamStateMultiService.firstRun) {
            aliveChangeCount.incrementAndGet();
        }
    }



    public void calculateRate() {

        if(StreamStateMultiService.firstRun) {
            return;
        }

        Instant now = Instant.now();
        long duration = Duration.between(lastCalculateRateTime.getAndSet(now), now).toSeconds();
        if(duration > 0) {
            int count = aliveChangeCount.getAndSet(0);
            double flapRate = (double)count / duration;
            rateStatistic.addValue(flapRate);
            flapRateMoving = rateStatistic.getMean();

            if(Math.abs(flapRateMoving) > STREAM_FLAPPING_MIN_RATE){
                log.trace("Stream {}, cnt: {},  freq: {}, moving: {}", streamKey, count, flapRate, flapRateMoving);
            }
        }
    }



    @Override
    public String toString() {
        return "StreamState{" +
            "streamKey=" + streamKey +
            ", enabled=" + enabled +
            ", alive=" + alive +
            '}';
    }
}













//
//    /**
//     * Check if stream status has been changed (going online/offline/flapping)
//     */
//    public Set<StreamEventType> calculateStatus(boolean updateAlive) {
//
//        Set<StreamEventType> result = new HashSet<>();
//
//        if(updateAlive) {
//            result.add(StreamEventType.ONLINE);
//        }
//        else {
//            result.add(StreamEventType.OFFLINE);
//        }
//        this.setAlive(updateAlive);
//
//        return result;
//
////
////        // ToDo: Flapping not calculated yet
////
////        // Calculating stream flapping -------------------------------
////
////        //Double flappingRate = state.calculateUpdateAliveFreq(update);
////
////        //log.info("Частота изменения StreamUpdate.alive: {}", updateAliveFreq);
////
//////        if(flappingRate != null) {
//////            log.info("ИЗМЕНЕНИЕ: Частота изменения StreamUpdate.alive: {}", flappingRate);
//////
//////            if(!stream.isFlapping() && flappingRate > STREAM_FLAPPING_MIN_RATE) {
//////                stream.setFlapping(true);
//////                result.add(StreamEventType.START_FLAPPING);
//////            }
//////
//////            if(stream.isFlapping() && flappingRate < STREAM_FLAPPING_MIN_RATE) {
//////                stream.setFlapping(false);
//////                result.add(StreamEventType.STOP_FLAPPING);
//////            }
//////        }
////
////        // Calculating stream online/offline changing -------------
////        int step = updateAlive ? +1 : -1;
////
////
////        // update stream.level
////        if(Math.abs(level + step) <= STREAM_MAX_LEVEL) {
////            level += step;
////        }
////
////        if(alive && level < -STREAM_THRESHOLD_LEVEL) {
////            alive = false;
////            result.add(StreamEventType.OFFLINE);
////        }
////
////        if(!alive && level > +STREAM_THRESHOLD_LEVEL) {
////            alive = true;
////            result.add(StreamEventType.ONLINE);
////        }
////        return result;
//    }



// Значение StreamUpdate.alive при последнем обновлении состояния стрима
//    @Getter
//    @Setter
//    private boolean lastUpdateAlive;
//
//    /**
//     * Вычисляет для стрима количество изменений StreamUpdate.alive в секунду
//     * <br>Для начала вычисления, необходимо,
//     * чтобы StreamUpdate.alive изменилось не менее, чем UPDATE_ALIVE_CHANGE_COUNT_MIN раз.
//     * @param event StreamUpdate
//     * @return null, если вычисление не производилось, double - результат
//     */
//    public Double calculateUpdateAliveFreq(MediaServerEvent event) {
//
//        Double result = null;
//
//        // Если update.alive изменилось
//        boolean alive = event.getEventType() == MediaServerEventType.SOURCE_READY ||
//                        event.getEventType() == MediaServerEventType.STREAM_STARTED;
//
//        if(lastUpdateAlive != alive) {
//
//
//            log.trace("Stream {} flap count: {}", event.getStreamName(), updateAliveChangeCount.get());
//
//            // увеличиваем aliveChangeCount
//            // Производим расчет частоты изменения updateAlive,
//            // только если количество изменений updateAlive с предыдущего вычисления calculateLevelFreq больше 10
//            if(updateAliveChangeCount.incrementAndGet() >= UPDATE_ALIVE_CHANGE_COUNT_MIN) {
//                Instant now = Instant.now();
//                Duration d = Duration.between(lastUpdateAliveCalcTime.getAndSet(now), now);
//                result =  (double) updateAliveChangeCount.getAndSet(0) / d.toSeconds();
//            }
//
//            // обновляем состояние lastUpdateAlive по данным из update
//            lastUpdateAlive = alive;
//        }
//        return result;
//    }



