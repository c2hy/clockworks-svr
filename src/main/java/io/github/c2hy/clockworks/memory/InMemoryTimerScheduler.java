package io.github.c2hy.clockworks.memory;

import io.github.c2hy.clockworks.core.timer.TimerScheduler;
import io.github.c2hy.clockworks.core.timer.TimerTriggerUseCase;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class InMemoryTimerScheduler implements TimerScheduler {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final InMemoryTimerRepository timerRepository;

    @Override
    public void start(TimerTriggerUseCase timerTriggerUseCase) {
        executor.scheduleAtFixedRate(() -> {
            var iterator = timerRepository.timerStore.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                if (entry.getValue().triggerAt().isBefore(Instant.now())) {
                    iterator.remove();
                    timerTriggerUseCase.onTrigger(entry.getValue());
                }
            }
        }, 1, 5, TimeUnit.SECONDS);
    }
}
