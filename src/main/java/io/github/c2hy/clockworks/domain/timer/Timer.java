package io.github.c2hy.clockworks.domain.timer;

import io.github.c2hy.clockworks.infrastructure.Changeable;
import io.github.c2hy.clockworks.infrastructure.Changes;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;

public class Timer implements Changeable {
    private final Changes changes = new Changes();

    @Getter
    private Integer id;
    @Getter
    private String definitionId;
    private TimerTypeEnum type;
    private String groupId;
    private OffsetDateTime triggerTime;
    private TimerStateEnum state;
    private String callbackUrl;

    private Timer() {
    }

    public static Timer create(String definitionId,
                               TimerTypeEnum type,
                               String groupId,
                               OffsetDateTime triggerTime,
                               String callbackUrl) {
        var timer = new Timer();
        timer.setDefinitionId(definitionId);
        timer.setType(type);
        timer.setGroupId(groupId);
        timer.setTriggerTime(triggerTime);
        timer.setState(TimerStateEnum.RUNNING);
        timer.setCallbackUrl(callbackUrl);
        return timer;
    }

    public static Timer createExisted(Integer id,
                                      String definitionId,
                                      TimerTypeEnum type,
                                      String groupId,
                                      OffsetDateTime triggerTime,
                                      TimerStateEnum state,
                                      String callbackUrl) {
        var timer = new Timer();
        timer.id = id;
        timer.definitionId = definitionId;
        timer.type = type;
        timer.groupId = groupId;
        timer.triggerTime = triggerTime;
        timer.state = state;
        timer.callbackUrl = callbackUrl;
        return timer;
    }

    public void notice(NotificationService notificationService) {
        notificationService.sendNotification(
                type == TimerTypeEnum.DELAY_EXECUTION,
                callbackUrl,
                definitionId + " " + type.name()
        );
    }

    public void setDefinitionId(String definitionId) {
        if (Objects.equals(this.definitionId, definitionId)) {
            return;
        }

        this.definitionId = definitionId;
        this.changes().put("definitionId", definitionId);
    }

    public void setType(TimerTypeEnum type) {
        if (Objects.equals(this.type, type)) {
            return;
        }

        this.type = type;
        this.changes().put("type", type);
    }

    public void setGroupId(String groupId) {
        if (Objects.equals(this.groupId, groupId)) {
            return;
        }

        this.groupId = groupId;
        this.changes().put("groupId", groupId);
    }

    public void setTriggerTime(OffsetDateTime triggerTime) {
        if (Objects.equals(this.triggerTime, triggerTime)) {
            return;
        }

        this.triggerTime = triggerTime;
        this.changes().put("triggerTime", triggerTime);
    }

    public void setState(TimerStateEnum state) {
        if (Objects.equals(this.state, state)) {
            return;
        }

        this.state = state;
        this.changes().put("state", state);
    }

    private void setCallbackUrl(String callbackUrl) {
        if (Objects.equals(this.callbackUrl, callbackUrl)) {
            return;
        }

        this.callbackUrl = callbackUrl;
        this.changes().put("callbackUrl", callbackUrl);
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
