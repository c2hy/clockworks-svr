package io.github.c2hy.clockworks.core.definition;

import jakarta.annotation.Nullable;

import java.time.Instant;

public record TimerDefinition(String id,
                              String groupId,
                              String key,
                              String callbackRouting,
                              @Nullable String cronExpression,
                              @Nullable Instant beginAt,
                              @Nullable Instant finishAt) {
}
