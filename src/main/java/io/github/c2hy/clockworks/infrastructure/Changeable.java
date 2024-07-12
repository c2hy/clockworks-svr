package io.github.c2hy.clockworks.infrastructure;

import java.util.Map;

public interface Changeable {
    void markOld();

    boolean isNew();

    Map<String, Object> changes();
}
