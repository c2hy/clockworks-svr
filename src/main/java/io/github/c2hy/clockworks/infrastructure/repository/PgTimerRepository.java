package io.github.c2hy.clockworks.infrastructure.repository;

import io.github.c2hy.clockworks.domain.timer.Timer;
import io.github.c2hy.clockworks.domain.timer.*;
import jakarta.annotation.Nullable;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.*;

import static io.github.c2hy.clockworks.infrastructure.repository.DbClient.multipleQuestionMark;
import static io.github.c2hy.clockworks.infrastructure.repository.DbClient.transaction;

public class PgTimerRepository implements TimerRepository {
    @Override
    public Collection<TimerDefinition> findAllById(Collection<String> timerDefinitionIds) {
        var handler = new ResultSetHandler<List<TimerDefinition>>() {
            @Override
            public List<TimerDefinition> handle(ResultSet resultSet) throws SQLException {
                var definitions = new ArrayList<TimerDefinition>();
                while (resultSet.next()) {
                    definitions.add(timerDefinitionSelectMapping(resultSet));
                }
                return definitions;
            }
        };

        String[] idsParam = timerDefinitionIds.toArray(new String[0]);
        return DbClient.select(
                "SELECT * FROM timer_definition WHERE id IN (" + multipleQuestionMark(timerDefinitionIds) + ")",
                handler,
                (Object[]) idsParam);
    }

    @Override
    public Optional<TimerDefinition> findById(String timerDefinitionId) {
        var handler = new ResultSetHandler<TimerDefinition>() {
            @Override
            public TimerDefinition handle(ResultSet resultSet) throws SQLException {
                if (!resultSet.next()) {
                    return null;
                }

                var row = resultSet.getRow();
                if (row != 1) {
                    throw new IllegalStateException("Expected 1 row, but got " + row);
                }

                return timerDefinitionSelectMapping(resultSet);
            }
        };

        var timerDefinition = DbClient.selectFirst("SELECT * FROM timer_definition WHERE id = ?", handler, timerDefinitionId);
        return Optional.ofNullable(timerDefinition);
    }

    private static TimerDefinition timerDefinitionSelectMapping(ResultSet resultSet) throws SQLException {
        return TimerDefinition.createExisted(
                resultSet.getString("id"),
                resultSet.getString("group_id"),
                TimerTypeEnum.valueOf(resultSet.getString("type")),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getInt("initial_delay_seconds"),
                resultSet.getInt("interval_seconds"),
                resultSet.getString("callback_url")
        );
    }

    @Override
    public void deleteByGroupId(String groupId, Collection<String> list) {
        var params = new ArrayList<>();
        params.add(groupId);
        params.addAll(list);
        transaction(connection -> {
            DbClient.delete("timer_definition", "group_id = ? AND id NOT IN (" + multipleQuestionMark(list) + ")", params.toArray());
            DbClient.delete("timer", "group_id = ?", groupId);
        });
    }

    @Override
    public void deleteByDefinitionId(String id) {
        DbClient.delete("timer_definition", "id = ?", id);
    }

    @Override
    public void save(Collection<TimerDefinition> timerDefinition) {
        var newDefinitions = new ArrayList<TimerDefinition>();
        var existedDefinitions = new ArrayList<TimerDefinition>();
        for (TimerDefinition definition : timerDefinition) {
            if (definition.changes().isEmpty()) {
                continue;
            }

            if (definition.isNew()) {
                newDefinitions.add(definition);
            } else {
                existedDefinitions.add(definition);
            }
        }

        DbClient.transaction(connection -> {
            if (!newDefinitions.isEmpty()) {
                DbClient.insertBatch("timer_definition", newDefinitions.stream().map(TimerDefinition::changes).toList());
            }
            if (!existedDefinitions.isEmpty()) {
                for (TimerDefinition definition : existedDefinitions) {
                    DbClient.update(connection, "timer_definition", definition.changes(), "id = ?", definition.getId());
                }
            }
        });
    }

    @Override
    public void save(TimerDefinition timerDefinition) {
        if (timerDefinition.isNew()) {
            DbClient.insertBatch("timer_definition", List.of(timerDefinition.changes()));
        } else {
            DbClient.update("timer_definition", timerDefinition.changes(), "id = ?", timerDefinition.getId());
        }
    }

    @Override
    public void createTimer(Collection<Timer> timers) {
        DbClient.insertBatch("timer", timers.stream().map(Timer::changes).toList());
    }

    @Override
    public void createTimer(Timer timer) {
        DbClient.insertBatch("timer", List.of(timer.changes()));
    }

    @Override
    public Collection<Integer> loadRunningTimerIds() {
        var handler = new ResultSetHandler<List<Integer>>() {
            @Override
            public List<Integer> handle(ResultSet resultSet) throws SQLException {
                var ids = new ArrayList<Integer>();
                while (resultSet.next()) {
                    ids.add(resultSet.getInt("id"));
                }
                return ids;
            }
        };
        return DbClient.select("SELECT id FROM timer WHERE state = ? AND trigger_time <= ?", handler, TimerStateEnum.RUNNING.name(), OffsetDateTime.now());
    }

    @Nullable
    @Override
    public Timer lockTimer(Integer timerId) {
        var updated = DbClient.update(
                "timer",
                Map.of("state", TimerStateEnum.NOTIFICATION.name()),
                "id = ? AND state = ?",
                timerId, TimerStateEnum.RUNNING.name());

        if (updated) {
            var handler = new ResultSetHandler<Timer>() {
                @Override
                public Timer handle(ResultSet resultSet) throws SQLException {
                    if (resultSet.next()) {
                        return timerSelectMapping(resultSet);
                    }
                    return null;
                }
            };

            return DbClient.selectFirst("SELECT * FROM timer WHERE id = ?", handler, timerId);
        }

        return null;
    }

    public static Timer timerSelectMapping(ResultSet resultSet) throws SQLException {
        return Timer.createExisted(
                resultSet.getInt("id"),
                resultSet.getString("definition_id"),
                TimerTypeEnum.valueOf(resultSet.getString("type")),
                resultSet.getString("group_id"),
                DbClient.timestampToOffsetDateTime(resultSet.getTimestamp("trigger_time")),
                TimerStateEnum.valueOf(resultSet.getString("state")),
                resultSet.getString("callback_url")
        );
    }
}
