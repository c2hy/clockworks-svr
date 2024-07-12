package io.github.c2hy.clockworks.infrastructure;

import java.util.HashMap;
import java.util.Map;

public class Changes implements Changeable {
    private final Map<String, Object> changed = new HashMap<>();
    private boolean isNew = true;

    public void markOld() {
        this.isNew = false;
    }

    public void changed(String key, Object value) {
        this.changed.put(key, value);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public Map<String, Object> changes() {
        return changed;
    }
}
