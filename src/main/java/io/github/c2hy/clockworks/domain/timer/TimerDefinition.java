package io.github.c2hy.clockworks.domain.timer;

import io.github.c2hy.clockworks.infrastructure.Changeable;
import io.github.c2hy.clockworks.infrastructure.Changes;
import io.github.c2hy.clockworks.infrastructure.Checkable;
import io.github.c2hy.clockworks.infrastructure.utils.ObjectUtils;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static io.github.c2hy.clockworks.infrastructure.utils.Checking.exceptOrThrow;
import static io.github.c2hy.clockworks.infrastructure.utils.ObjectUtils.isNotEmpty;

public class TimerDefinition implements Checkable, Changeable {
    private static final int MIN_INITIAL_DELAY_SECONDS = Integer.parseInt(
            System.getProperty("MIN_INITIAL_DELAY_SECONDS", "3")
    );
    private static final int MIN_INTERVAL_SECONDS = Integer.parseInt(
            System.getProperty("MIN_INTERVAL_SECONDS", "5")
    );

    @Delegate
    private final Changes changes = new Changes();

    @Getter
    private String id;
    private String groupId;
    private TimerTypeEnum type;
    private String name;
    private String description;
    private int initialDelaySeconds;
    private int intervalSeconds;
    private String callbackUrl;

    private TimerDefinition() {
    }

    public static TimerDefinition merge(TimerDefinitionDTO timerDefinitionDTO,
                                        @Nullable TimerDefinition existedTimerDefinition,
                                        String groupId) {
        var timerDefinition = Optional.ofNullable(existedTimerDefinition).orElseGet(TimerDefinition::new);

        timerDefinition.setId(timerDefinitionDTO.getId());
        if (ObjectUtils.isNotEmpty(groupId)) {
            timerDefinition.setGroupId(groupId);
        } else {
            timerDefinition.setGroupId(timerDefinitionDTO.getId());
        }
        timerDefinition.setType(timerDefinitionDTO.getType());
        if (ObjectUtils.isNotEmpty(timerDefinitionDTO.getName())) {
            timerDefinition.setName(timerDefinitionDTO.getName());
        } else {
            timerDefinition.setName(Instant.now() + " " + new Random().ints(5));
        }
        timerDefinition.setDescription(timerDefinitionDTO.getDescription());
        timerDefinition.setInitialDelaySeconds(timerDefinitionDTO.getInitialDelaySeconds());
        timerDefinition.setIntervalSeconds(timerDefinitionDTO.getIntervalSeconds());
        timerDefinition.setCallbackUrl(timerDefinitionDTO.getCallbackUrl());
        return timerDefinition;
    }

    public static TimerDefinition createExisted(String id,
                                                String groupId,
                                                TimerTypeEnum type,
                                                String name,
                                                String description,
                                                int initialDelaySeconds,
                                                int intervalSeconds,
                                                String callbackUrl) {
        var timerDefinition = new TimerDefinition();
        timerDefinition.id = id;
        timerDefinition.groupId = groupId;
        timerDefinition.type = type;
        timerDefinition.name = name;
        timerDefinition.description = description;
        timerDefinition.initialDelaySeconds = initialDelaySeconds;
        timerDefinition.intervalSeconds = intervalSeconds;
        timerDefinition.callbackUrl = callbackUrl;
        timerDefinition.markOld();
        return timerDefinition;
    }

    public Timer firstTimer() {
        var interval = Math.max(intervalSeconds, 0);

        return Timer.create(
                this.id,
                this.type,
                this.groupId,
                OffsetDateTime.now().plusSeconds(interval + intervalSeconds),
                this.callbackUrl
        );
    }

    @Nullable
    public Timer nextTimer() {
        if (intervalSeconds == -1) {
            return null;
        }

        return Timer.create(
                this.id,
                this.type,
                this.groupId,
                OffsetDateTime.now().plusSeconds(intervalSeconds),
                this.callbackUrl
        );
    }

    @Override
    public void check() {
        exceptOrThrow(
                isNotEmpty(id),
                "illegal timer definition id"
        );
        exceptOrThrow(
                isNotEmpty(groupId),
                "illegal groupId"
        );
        exceptOrThrow(
                name.length() < 30,
                "illegal name"
        );
        exceptOrThrow(
                description.length() < 100,
                "illegal description"
        );
        exceptOrThrow(
                initialDelaySeconds == -1 || initialDelaySeconds > MIN_INITIAL_DELAY_SECONDS,
                "illegal initialDelaySeconds"
        );
        exceptOrThrow(
                intervalSeconds == -1 || intervalSeconds > MIN_INTERVAL_SECONDS,
                "illegal intervalSeconds"
        );
    }

    private void setId(String id) {
        if (Objects.equals(this.id, id)) {
            return;
        }

        this.id = id;
        this.changes.changed("id", id);
    }

    private void setGroupId(String groupId) {
        exceptOrThrow(isNotEmpty(id), "illegal id");

        if (Objects.equals(this.groupId, groupId)) {
            return;
        }

        this.groupId = groupId;
        this.changes.changed("groupId", groupId);
    }

    private void setType(TimerTypeEnum type) {
        if (Objects.equals(this.type, type)) {
            return;
        }

        this.type = type;
        this.changes.changed("type", type);
    }

    private void setName(String name) {
        if (Objects.equals(this.name, name)) {
            return;
        }

        this.name = name;
        this.changes.changed("name", name);
    }

    private void setDescription(String description) {
        if (Objects.equals(this.description, description)) {
            return;
        }

        this.description = description;
        this.changes.changed("description", description);
    }

    private void setInitialDelaySeconds(int initialDelaySeconds) {
        if (Objects.equals(this.initialDelaySeconds, initialDelaySeconds)) {
            return;
        }

        this.initialDelaySeconds = initialDelaySeconds;
        this.changes.changed("initialDelaySeconds", initialDelaySeconds);
    }

    private void setIntervalSeconds(int intervalSeconds) {
        if (Objects.equals(this.intervalSeconds, intervalSeconds)) {
            return;
        }

        this.intervalSeconds = intervalSeconds;
        this.changes.changed("intervalSeconds", intervalSeconds);
    }

    public void setCallbackUrl(String callbackUrl) {
        if (Objects.equals(this.callbackUrl, callbackUrl)) {
            return;
        }

        this.callbackUrl = callbackUrl;
        this.changes.changed("callbackUrl", callbackUrl);
    }

    @Override
    public void markOld() {
        changes.markOld();
    }

    @Override
    public boolean isNew() {
        return changes.isNew();
    }

    @Override
    public Map<String, Object> changes() {
        return changes.changes();
    }
}