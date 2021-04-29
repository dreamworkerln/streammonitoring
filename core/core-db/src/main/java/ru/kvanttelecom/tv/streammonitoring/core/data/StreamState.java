package ru.kvanttelecom.tv.streammonitoring.core.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;
import ru.kvanttelecom.tv.streammonitoring.utils.dto.enums.StreamStateTypes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Internal stream status info
 */
@Slf4j
public class StreamState extends AbstractEntity {

    // private static final int STREAM_MAX_LEVEL = 10;
    // private static final int STREAM_THRESHOLD_LEVEL = (int)(STREAM_MAX_LEVEL * 0.7);

    // минимальный порог фильтрации частоты изменения alive
    // при значениях частоты, ниже указанного стрим не будет отображен как флапающий
    // public static final double STREAM_FLAPPING_MIN_RATE = 1e-3; // Minimum detectable rate to stream flapping  (1 раз в 1000 сек)

    // при значениях частоты, выше указанного стрим будет рассматриваться как серьезно флапающий
    // И такой стрим не будет валиться в streamEventSender - события о стримах (группа оповещений в телеграм)
    // public static final double STREAM_FLAPPING_MIN_VALUABLE_RATE = 1e-2; // Уровень значимого флапа

//    // Порог минимального числа изменений alive
//    // для начала вычисления частоты изменения alive стрима
//    private static final int UPDATE_ALIVE_CHANGE_COUNT_MIN = 10;

    @Getter
    private final StreamKey streamKey;


    private final ConcurrentMap<StreamStateTypes, SubState> substates = new ConcurrentHashMap<>();


    public StreamState(StreamKey streamKey, boolean enabled, boolean alive) {
        this.streamKey = streamKey;

        substates.put(StreamStateTypes.ENABLENESS, new SubState(enabled));
        substates.put(StreamStateTypes.ALIVENESS, new SubState(alive));
    }

    public boolean isEnabled() {
        return substates.get(StreamStateTypes.ENABLENESS).isValue();
    }


    public boolean isAlive() {
        return substates.get(StreamStateTypes.ALIVENESS).isValue();
    }

    /**
     * Выдает минимальный период по всем типам событий
     */
    public double getPeriod() {
        AtomicReference<Double> result = new AtomicReference<>(0.);
        substates.values().stream().mapToDouble(SubState::getPeriod).min().ifPresent(result::set);
        return result.get();
    }


    public boolean update(StreamStateTypes subtype, boolean newValue) {

        boolean result;

        SubState substate = substates.get(subtype);
        log.trace("{}", streamKey);
        result = substate.update(newValue);
        return result;
    }


    @Override
    public String toString() {
        return "StreamState{" +
            "streamKey=" + streamKey +
            ", enabled=" + substates.get(StreamStateTypes.ENABLENESS).isValue() +
            ", alive=" + substates.get(StreamStateTypes.ALIVENESS).isValue() +
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



