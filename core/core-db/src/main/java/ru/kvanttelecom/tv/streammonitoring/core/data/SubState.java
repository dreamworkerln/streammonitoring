package ru.kvanttelecom.tv.streammonitoring.core.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static ru.kvanttelecom.tv.streammonitoring.core.services.caching.StreamStateMultiService.STREAM_FLAPPING_MIN_PERIOD;


@Slf4j
public class SubState {

    private static int WINDOW_SIZE = 20;
    // Стримы, флапающее с большим периодом не отображаются
    //public static double STREAM_FLAPPING_MIN_PERIOD = 1e100;


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

    private final AtomicReference<Instant> lastUpTime = new AtomicReference<>(Instant.EPOCH);
    private final AtomicReference<Instant> lastDownTime = new AtomicReference<>(Instant.EPOCH);

    public SubState(boolean initial) {
        this.value = initial;
        upStatistic.setWindowSize(WINDOW_SIZE);
        downStatistic.setWindowSize(WINDOW_SIZE);
    }

    public double getPeriod() {
        double period = (upStatistic.getMean() +  downStatistic.getMean()) / 2;
        return Double.isNaN(period) ? STREAM_FLAPPING_MIN_PERIOD + 1 : period;
    }

    /**
     * Update substate frequency
     * @param newValue
     * @return notification required
     */
    public boolean update(boolean newValue) {

        boolean result = false;

        log.trace("old: {}, new: {}", value, newValue);
        value = newValue;

        AtomicReference<Instant> lastTime;
        DescriptiveStatistics statistics;
        if(value) {
            lastTime = lastUpTime;
            statistics = upStatistic;
            log.trace("up");
        }
        else {
            lastTime = lastDownTime;
            statistics = downStatistic;
            log.trace("down");
        }

        return process(lastTime, statistics);

    }

    /**
     *
     * @param lastTime
     * @param statistics
     * @return notification required
     */
    private boolean process(AtomicReference<Instant> lastTime, DescriptiveStatistics statistics) {
        boolean result = true;

        Instant now = Instant.now();

        log.trace("lastTime: {}", lastTime.get());
        double duration = lastTime.get().equals(Instant.EPOCH) ? 0 :
            Duration.between(lastTime.get(), now).toMillis() / 1000.0;

        lastTime.set(now);
        log.trace("duration: {}", duration);

        if (duration > 0) {
            double period = statistics.getMean();
            double std = statistics.getStandardDeviation();
            if (!Double.isNaN(period)) {
                log.trace("period: {}, std: {}", period, std);
                // правило 3 сигм
                log.trace("Math.abs(period - duration) > 3 * std: {} > {}", Math.abs(period - duration), 3 * std);
                result = Math.abs(period - duration) > 3 * std;
            }
            statistics.addValue(duration);
        }        
        log.info("result: {}", result);
        log.trace("========================");
        return result;
    }
}



// ASAP EDC: move this to upper
//        if(StreamStateMultiService.firstRun) {
//            return;
//        }





//
//    public void calculateRate() {
//
//        // ASAP EDC: move this to upper
//        if(StreamStateMultiService.firstRun) {
//            return;
//        }
//
//
//
//        Instant now = Instant.now();
//        long duration = Duration.between(lastRateCalculationTime.getAndSet(now), now).toSeconds();
//        if(duration > 0) {
//            int count = changeCount.getAndSet(0);
//            double rate = (double)count / duration;
//            rateStatistic.addValue(rate);
//            this.rate = rateStatistic.getMean();
//        }
//    }

