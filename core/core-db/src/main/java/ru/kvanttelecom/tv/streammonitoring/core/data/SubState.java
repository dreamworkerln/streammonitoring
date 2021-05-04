package ru.kvanttelecom.tv.streammonitoring.core.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class SubState {

    // Стримы, флапающее с большим периодом не отображаются,
    // но сбор статистики по таким стримам будет продолжен
    public static double STREAM_FLAPPING_MAX_PERIOD_SECONDS = 60;

    // размер окна скользящего среднего
    private static int WINDOW_SIZE = 20;

    @Getter
    private boolean value;

//    /**
//     * flapping value period
//     */
//    @Getter
//    private double period = INITIAL_PERIOD;

    // ---------------------------------------------------------------------------------------------

    private final DescriptiveStatistics upStatistic = new DescriptiveStatistics();
    private final DescriptiveStatistics downStatistic = new DescriptiveStatistics();

    // уровень для гистерезиса
    private int level = 0;

    // Количество изменений value с момента предыдущего расчета частоты
    // how many times value was changed since last calculation
    //private final AtomicInteger changeCount = new AtomicInteger();

    // время последнего вычисления периода
    // private final AtomicReference<Instant> lastPeriodCalculationTime = new AtomicReference<>(Instant.now());

    private final AtomicReference<Instant> lastUpTime = new AtomicReference(Instant.EPOCH);
    private final AtomicReference<Instant> lastDownTime = new AtomicReference(Instant.EPOCH);

    public SubState(boolean initial) {
        this.value = initial;
        upStatistic.setWindowSize(WINDOW_SIZE);
        downStatistic.setWindowSize(WINDOW_SIZE);
    }

    public double getPeriod() {

        double period = (upStatistic.getMean() +  downStatistic.getMean()) / 2;
        period = Double.isNaN(period) ? STREAM_FLAPPING_MAX_PERIOD_SECONDS + 1 : period;


        // долго не поступало событий со стрима, продолжаем измерять период
        // (период измерится, когда появятся новые данные), но пользователю говорим
        // что период вышел за границы измерений
        Instant max = lastUpTime.get();
        if(lastDownTime.get().getEpochSecond() > max.getEpochSecond()) {
            max = lastDownTime.get();
        }

        if (Duration.between(max, Instant.now()).toSeconds() > STREAM_FLAPPING_MAX_PERIOD_SECONDS) {
            period = STREAM_FLAPPING_MAX_PERIOD_SECONDS + 1;
        }
        return period;
    }

    /**
     * Update substate frequency
     * @param newValue
     * @return substate have been changed
     */
    public boolean update(boolean newValue) {

        boolean result = false;

        log.debug("old: {}, new: {}", value, newValue);
        value = newValue;

        AtomicReference<Instant> lastTime;
        DescriptiveStatistics statistics;
        if(value) {
            lastTime = lastUpTime;
            statistics = upStatistic;
            log.debug("up");
        }
        else {
            lastTime = lastDownTime;
            statistics = downStatistic;
            log.debug("down");
        }

        return process(lastTime, statistics);

    }

    /**
     *
     * @param lastTime
     * @param statistics
     * @return does state changed
     */
    private boolean process(AtomicReference<Instant> lastTime, DescriptiveStatistics statistics) {
        boolean result = true;

        Instant now = Instant.now();

        log.debug("lastTime: {}", lastTime.get());
        double duration = lastTime.get().equals(Instant.EPOCH) ? 0 :
            Duration.between(lastTime.get(), now).toMillis() / 1000.0;

        lastTime.set(now);
        log.debug("duration: {}", duration);

        if (duration > 0) {
            double period = statistics.getMean();
            double std = statistics.getStandardDeviation();
            log.debug("period: {}", period);
            if (!Double.isNaN(period)) {
                log.debug("period: {}, std: {}", period, std);
                // правило 3 сигм
                log.debug("Math.abs(period - duration) > 3 * std: {} > {}", Math.abs(period - duration), 3 * std);
                result = Math.abs(period - duration) > 3 * std;
            }
            statistics.addValue(duration);
        }        
        log.debug("result: {}", result);
        log.debug("========================");
        return result;
    }
}
