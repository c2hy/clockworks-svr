package io.github.c2hy.clockworks.core.common;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public final class ObjectUtils {
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static boolean isEmpty(Object obj) {
        switch (obj) {
            case null -> {
                return true;
            }
            case String s -> {
                return s.isEmpty();
            }
            case CharSequence charSequence -> {
                return charSequence.isEmpty();
            }
            default -> {
            }
        }

        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }

        if (obj instanceof Collection<?>) {
            return ((Collection<?>) obj).isEmpty();
        }

        if (obj instanceof Map<?, ?>) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        return false;
    }
}
