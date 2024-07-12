package io.github.c2hy.clockworks.application;

import io.github.c2hy.clockworks.domain.timer.TimerService;
import lombok.RequiredArgsConstructor;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ScheduleTimerScope {
    private static final int INTERVAL_SECONDS = Integer.parseInt(System.getProperty("INTERVAL_SECONDS", "3"));

    private final TimerService timerService;

    public void fixedTimeTrigger() {
        var executorService = Executors.newSingleThreadScheduledExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));

        var random = new Random();
        int initialDelaySeconds = random.nextInt(6);

        executorService.scheduleAtFixedRate(() -> {
            var timers = timerService.loadRunningTimerIds();
            if (timers.isEmpty()) {
                return;
            }

            timers.parallelStream().forEach(timerService::triggerTimer);
        }, initialDelaySeconds, INTERVAL_SECONDS, TimeUnit.SECONDS);
    }
}
