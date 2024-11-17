package io.github.c2hy.clockworks.core.definition;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class TimerDefinitionSaveResult {
    private TimerDefinition newDefinition;
    @Nullable
    private String deleteDefinitionId;
}
