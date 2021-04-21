package ru.kvanttelecom.tv.streammonitoring.tbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.utils.startuprunner.BaseStartupRunner;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


@Component
@Slf4j
public class TBotStartupRunner extends BaseStartupRunner {
    @Override
    public void run(ApplicationArguments args) {
        super.run(args);
    }
}
