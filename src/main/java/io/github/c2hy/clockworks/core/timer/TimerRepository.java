package io.github.c2hy.clockworks.core.timer;

import java.util.List;

public interface TimerRepository {
    void save(Timer nextTimer);

    void deleteByTimerDefinitionIds(List<String> deletedTimerDefinitionIds);

    void saveAll(List<Timer> newTimers);
}
