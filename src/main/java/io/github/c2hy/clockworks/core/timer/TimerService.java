package io.github.c2hy.clockworks.core.timer;

import io.github.c2hy.clockworks.core.definition.TimerDefinition;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TimerService {
    private final TimerRepository timerRepository;

    public void deleteByTimerDefinitionIds(@Nonnull List<String> deletedTimerDefinitionIds) {
        if (deletedTimerDefinitionIds.isEmpty()) {
            return;
        }

        log.debug("delete timers by deleted timer definition ids: {}", deletedTimerDefinitionIds);
        timerRepository.deleteByTimerDefinitionIds(deletedTimerDefinitionIds);
    }

    public void createByTimerDefinition(@Nonnull List<TimerDefinition> newTimerDefinitions) {
        if (newTimerDefinitions.isEmpty()) {
            return;
        }

        log.debug("create timers by new timer definitions: {}", newTimerDefinitions
                .stream()
                .map(TimerDefinition::id)
                .toList());

        var newTimers = newTimerDefinitions.stream().map(Timer::of).toList();
        timerRepository.saveAll(newTimers);
    }
}
