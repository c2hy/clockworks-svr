package io.github.c2hy.clockworks.infrastructure.repository;

import io.github.c2hy.clockworks.domain.group.Group;
import io.github.c2hy.clockworks.domain.group.GroupRepository;
import io.github.c2hy.clockworks.infrastructure.utils.Extensions;
import lombok.experimental.ExtensionMethod;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;

@ExtensionMethod({Extensions.class})
public class PgGroupRepository implements GroupRepository {
    @Override
    public Optional<Group> findById(String groupId) {
        var handler = new ResultSetHandler<Group>() {
            @Override
            public Group handle(ResultSet resultSet) throws SQLException {
                if (!resultSet.next()) {
                    return null;
                }

                var row = resultSet.getRow();
                if (row != 1) {
                    throw new IllegalStateException("Expected 1 row, but got " + row);
                }

                return Group.createExisted(
                        resultSet.getString(Group.Fields.id),
                        resultSet.getString(Group.Fields.name),
                        resultSet.getString(Group.Fields.description),
                        resultSet.getInt(Group.Fields.updateIntervalSeconds.toLowerUnderscore()),
                        resultSet.getTimestamp(Group.Fields.lastUpdateTime.toLowerUnderscore()).toOffsetDateTime());
            }
        };

        var group = DbClient.selectFirst(
                "SELECT * FROM timer_group WHERE id = ?",
                handler,
                groupId
        );
        return Optional.ofNullable(group);
    }

    @Override
    public void save(Group group) {
        var changes = group.changes();

        if (changes.isEmpty()) {
            return;
        }

        if (group.isNew()) {
            DbClient.insertBatch("timer_group", Collections.singletonList(changes));
        } else {
            DbClient.update("timer_group", changes, "id = ?", group.getId());
        }
    }
}
