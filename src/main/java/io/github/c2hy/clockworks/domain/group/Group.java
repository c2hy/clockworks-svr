package io.github.c2hy.clockworks.domain.group;

import io.github.c2hy.clockworks.infrastructure.Changeable;
import io.github.c2hy.clockworks.infrastructure.Changes;
import io.github.c2hy.clockworks.infrastructure.Checkable;
import io.github.c2hy.clockworks.infrastructure.utils.Checking;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldNameConstants;

import java.time.OffsetDateTime;
import java.util.Objects;

import static io.github.c2hy.clockworks.infrastructure.utils.ObjectUtils.isNotEmpty;

@ExtensionMethod({Checking.class})
@FieldNameConstants
public class Group implements Checkable, Changeable {
    @Delegate
    private final Changes changes = new Changes();

    @Getter
    private String id;
    private String name;
    private String description;
    private int updateIntervalSeconds;
    private OffsetDateTime lastUpdateTime;

    private Group() {
    }

    public static Group createNullGroup() {
        return new Group();
    }

    public static Group create(GroupDTO groupDTO) {
        var group = new Group();
        group.setId(groupDTO.getId());
        group.setName(groupDTO.getName());
        group.setDescription(groupDTO.getDescription());
        group.setUpdateIntervalSeconds(groupDTO.getUpdateIntervalSeconds());
        group.setLastUpdateTime(OffsetDateTime.now());
        return group;
    }

    public static Group createExisted(String id,
                                      String name,
                                      String description,
                                      int updateIntervalSeconds,
                                      OffsetDateTime lastUpdateTime) {
        var group = new Group();
        group.id = id;
        group.name = name;
        group.description = description;
        group.updateIntervalSeconds = updateIntervalSeconds;
        group.lastUpdateTime = lastUpdateTime;
        group.markOld();
        return group;
    }

    public Group merge(GroupDTO groupDTO) {
        this.setId(groupDTO.getId());
        this.setName(groupDTO.getName());
        this.setDescription(groupDTO.getDescription());
        this.setUpdateIntervalSeconds(groupDTO.getUpdateIntervalSeconds());
        this.setLastUpdateTime(OffsetDateTime.now());
        return this;
    }

    public boolean ignoreUpdate() {
        if (id == null) {
            return false;
        }

        return lastUpdateTime.plusSeconds(updateIntervalSeconds)
                .isBefore(OffsetDateTime.now());
    }

    @Override
    public void check() {
        "illegal id".trueOrThrow(isNotEmpty(id));
        "illegal name".trueOrThrow(name.length() < 15);
        "illegal description".trueOrThrow(description == null || description.length() < 100);
    }

    private void setId(String id) {
        if (Objects.equals(this.id, id)) {
            return;
        }

        this.id = id;
        this.changes.changed(Fields.id, id);
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
        this.changes.changed(Fields.description, description);
    }

    private void setUpdateIntervalSeconds(int updateIntervalSeconds) {
        if (this.updateIntervalSeconds == updateIntervalSeconds) {
            return;
        }

        this.updateIntervalSeconds = updateIntervalSeconds;
        this.changes.changed(Fields.updateIntervalSeconds, updateIntervalSeconds);
    }

    private void setLastUpdateTime(OffsetDateTime now) {
        if (this.lastUpdateTime == now) {
            return;
        }

        this.lastUpdateTime = now;
        this.changes.changed(Fields.lastUpdateTime, now);
    }
}
