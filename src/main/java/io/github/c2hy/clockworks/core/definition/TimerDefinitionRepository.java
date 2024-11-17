package io.github.c2hy.clockworks.core.definition;


import java.util.Optional;

public interface TimerDefinitionRepository {
    Optional<TimerDefinition> findByGroupIdAndKey(String groupId, String key);

    boolean deleteById(String id);

    TimerDefinition save(TimerDefinition timerDefinition);
}
