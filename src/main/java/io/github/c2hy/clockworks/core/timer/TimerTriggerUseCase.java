package io.github.c2hy.clockworks.core.timer;


import io.github.c2hy.clockworks.core.common.CallbackInvoker;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TimerTriggerUseCase {
    private final CallbackInvoker callbackInvoker;
    private final TimerRepository timerRepository;

    public void onTrigger(@Nonnull Timer timer) {
        log.debug("trigger timer {} {}", timer.id(), timer.triggerAt());

        try {
            callbackInvoker.invoke(timer.callbackRouting(), timer.key());
        } catch (Exception e) {
            log.error("{} {} callback invoke failed", timer.definitionId(), timer.id(), e);
        }

        var nextTimer = timer.nextTimer();
        if (nextTimer != null) {
            log.debug("save next timer {} {}", nextTimer.id(), nextTimer.triggerAt());
            timerRepository.save(nextTimer);
        }
    }
}
