package io.github.c2hy.clockworks.infrastructure.repository;

import io.github.c2hy.clockworks.domain.group.Group;
import io.github.c2hy.clockworks.domain.group.GroupRepository;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;

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
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("update_interval_seconds"),
                        DbClient.timestampToOffsetDateTime(resultSet.getTimestamp("last_update_time")));
            }
        };

        var group = DbClient.selectFirst(
                "SELECT id,name,description,update_interval_seconds,last_update_time FROM timer_group WHERE id = ?",
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
