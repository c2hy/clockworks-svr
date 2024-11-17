package io.github.c2hy.clockworks.core.timer;

import io.github.c2hy.clockworks.core.common.CronUtils;
import io.github.c2hy.clockworks.core.common.ObjectUtils;
import io.github.c2hy.clockworks.core.definition.TimerDefinition;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.UUID;

public record Timer(String id,
                    Instant triggerAt,
                    String definitionId,
                    String key,
                    @Nullable String cronExpression,
                    String callbackRouting,
                    @Nullable Instant beginAt,
                    @Nullable Instant finishAt) {
    public static Timer of(TimerDefinition v) {
        return new Timer(
                UUID.randomUUID().toString(),
                CronUtils.next(v.cronExpression()),
                v.id(),
                v.key(),
                v.cronExpression(),
                v.callbackRouting(),
                v.beginAt(),
                v.finishAt()
        );
    }

    @Nullable
    public Timer nextTimer() {
        if (ObjectUtils.isEmpty(cronExpression)) {
            return null;
        }

        if (beginAt != null && Instant.now().isBefore(beginAt)) {
            return null;
        }

        if (finishAt != null && Instant.now().isAfter(finishAt)) {
            return null;
        }

        var nextTime = CronUtils.next(cronExpression);
        return new Timer(
                UUID.randomUUID().toString(),
                nextTime,
                definitionId,
                key,
                cronExpression,
                callbackRouting,
                beginAt,
                finishAt
        );
    }
}
