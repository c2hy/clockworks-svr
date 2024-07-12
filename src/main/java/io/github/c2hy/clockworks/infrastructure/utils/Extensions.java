package io.github.c2hy.clockworks.infrastructure.utils;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class Extensions {
    public static String toLowerUnderscore(String str) {
        return str.replaceAll("([A-Z])", "_$1").toLowerCase();
    }

    public static OffsetDateTime toOffsetDateTime(Timestamp timestamp) {
        return timestamp.toInstant().atOffset(OffsetDateTime.now().getOffset());
    }

    public static String questionMark(Collection<?> collection) {
        return collection.stream().map(key -> "?").collect(Collectors.joining(", "));
    }
}
