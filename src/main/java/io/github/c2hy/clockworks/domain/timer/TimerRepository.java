package io.github.c2hy.clockworks.domain.timer;

import jakarta.annotation.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TimerRepository {
    Collection<TimerDefinition> findAllById(Collection<String> timerDefinitionIds);

    Optional<TimerDefinition> findById(String timerDefinitionId);

    void deleteByGroupId(String groupId, Collection<String> list);

    void deleteByDefinitionId(String id);

    void save(Collection<TimerDefinition> timerDefinition);

    void save(TimerDefinition timerDefinition);

    void createTimer(Collection<Timer> timers);

    void createTimer(Timer timer);

    Collection<Integer> loadRunningTimerIds();

    @Nullable
    Timer lockTimer(Integer timerId);
}
