package io.github.c2hy.clockworks.memory;

import io.github.c2hy.clockworks.core.timer.Timer;
import io.github.c2hy.clockworks.core.timer.TimerRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class InMemoryTimerRepository implements TimerRepository {
    protected final Map<String, Timer> timerStore = new ConcurrentHashMap<>();

    @Override
    public void save(Timer timer) {
        timerStore.put(timer.definitionId(), timer);
    }

    @Override
    public void deleteByTimerDefinitionIds(List<String> deletedTimerDefinitionIds) {
        for (String deletedTimerDefinitionId : deletedTimerDefinitionIds) {
            timerStore.remove(deletedTimerDefinitionId);
        }
    }

    @Override
    public void saveAll(List<Timer> timers) {
        for (Timer timer : timers) {
            save(timer);
        }
    }
}
