package io.github.c2hy.clockworks.infrastructure.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.Nullable;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.github.c2hy.clockworks.infrastructure.Configures.*;

public final class DbClient {
    private final static Logger logger = LoggerFactory.getLogger(DbClient.class);
    private final static HikariDataSource dataSource;

    static {
        var config = new HikariConfig();
        config.setJdbcUrl(System.getProperty(
                CLOCKWORKS_DB_URL,
                "jdbc:postgresql://localhost:5432/clockworks"
        ));
        config.setUsername(System.getProperty(
                CLOCKWORKS_DB_USER,
                "postgres"
        ));
        config.setPassword(System.getProperty(
                CLOCKWORKS_DB_PASSWORD,
                "postgresql"
        ));
        config.addDataSourceProperty("cachePrepStmts", System.getProperty(
                CLOCKWORKS_DB_CACHE_PREP_STMTS,
                "true"
        ));
        config.addDataSourceProperty("prepStmtCacheSqlLimit", System.getProperty(
                CLOCKWORKS_DB_PREP_STMT_CACHE_SIZE,
                "250"
        ));
        config.addDataSourceProperty("prepStmtCacheSqlLimit", System.getProperty(
                CLOCKWORKS_DB_PREP_STMT_CACHE_SQL_LIMIT,
                "2048"
        ));
        dataSource = new HikariDataSource(config);
    }

    @Nullable
    public static <T> T selectFirst(String sql, ResultSetHandler<T> handler, Object... params) {
        var runner = new QueryRunner(dataSource);
        logger.debug("SELECT FIRST: {}", sql);

        try {
            return runner.query(sql, handler, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> select(String sql, ResultSetHandler<List<T>> handler, Object... params) {
        var runner = new QueryRunner(dataSource);
        logger.debug("SELECT: {}", sql);

        try {
            return runner.query(sql, handler, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void transaction(Consumer<Connection> consumer) {
        try (var connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            consumer.accept(connection);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertBatch(String table, List<Map<String, Object>> data) {
        try (var connection = dataSource.getConnection()) {
            insertBatch(connection, table, data);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertBatch(Connection connection, String table, List<Map<String, Object>> data) {
        if (data.isEmpty()) {
            return;
        }

        var firstData = data.get(0);
        var keys = new ArrayList<>(firstData.keySet());
        var fields = String.join(", ", keys.stream().map(DbClient::camelCaseToUnderscore).toList());
        var questionMark = multipleQuestionMark(keys);
        var sql = "INSERT INTO " + table + " (" + fields + ") VALUES " + "(" + questionMark + ")";
        var params = data.stream()
                .map(map -> keys.stream()
                        .map(v -> {
                            var value = map.get(v);
                            if (value instanceof Enum<?> enumObject) {
                                return enumParamToString(enumObject);
                            } else {
                                return value;
                            }
                        })
                        .toArray())
                .toArray(Object[][]::new);

        var handler = new BeanListHandler<>(String.class);

        var runner = new QueryRunner();
        logger.debug("INSERT: {}", sql);

        try {
            runner.insertBatch(connection, sql, handler, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean update(String table, Map<String, Object> data, String where, Object... whereParams) {
        try (var connection = dataSource.getConnection()) {
            return update(connection, table, data, where, whereParams);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean update(Connection connection, String table, Map<String, Object> data, String where, Object... whereParams) {
        if (data.isEmpty()) {
            return false;
        }

        var keys = new ArrayList<>(data.keySet());
        var questionMark = keys.stream()
                .map(key -> DbClient.camelCaseToUnderscore(key) + " = ?")
                .collect(Collectors.joining(", "));

        var params = new ArrayList<>();
        for (var key : keys) {
            params.add(data.get(key));
        }
        params.addAll(Arrays.asList(whereParams));
        var sql = "UPDATE " + table + " SET " + questionMark + " WHERE " + where;

        var runner = new QueryRunner();
        logger.debug("UPDATE: {}", sql);

        try {
            var update = runner.update(connection, sql, params.toArray());
            return update != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(String table, String where, Object... whereParams) {
        try (var connection = dataSource.getConnection()) {
            delete(connection, table, where, whereParams);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(Connection connection, String table, String where, Object... whereParams) {
        var sql = "DELETE FROM " + table + " WHERE " + where;
        var runner = new QueryRunner();
        logger.debug("DELETE: {}", sql);

        try {
            runner.update(connection, sql, whereParams);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String camelCaseToUnderscore(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    public static OffsetDateTime timestampToOffsetDateTime(Timestamp timestamp) {
        return timestamp.toInstant().atOffset(OffsetDateTime.now().getOffset());
    }

    public static String multipleQuestionMark(Collection<?> collection) {
        return collection.stream().map(key -> "?").collect(Collectors.joining(", "));
    }

    public static String enumParamToString(Enum<?> enumObject) {
        return enumObject.name();
    }
}
