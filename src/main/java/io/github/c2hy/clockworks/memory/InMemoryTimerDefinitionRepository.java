package io.github.c2hy.clockworks.memory;

import io.github.c2hy.clockworks.core.definition.TimerDefinition;
import io.github.c2hy.clockworks.core.definition.TimerDefinitionRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTimerDefinitionRepository implements TimerDefinitionRepository {
    private final Map<String, TimerDefinition> store = new ConcurrentHashMap<>();

    @Override
    public Optional<TimerDefinition> findByGroupIdAndKey(String groupId, String key) {
        return store.values().stream().findAny().filter(v -> v.groupId().equals(groupId) && v.key().equals(key));
    }

    @Override
    public boolean deleteById(String id) {
        return store.remove(id) != null;
    }

    @Override
    public TimerDefinition save(TimerDefinition timerDefinition) {
        store.put(timerDefinition.id(), timerDefinition);
        return timerDefinition;
    }
}
