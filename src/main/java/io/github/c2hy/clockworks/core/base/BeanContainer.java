package io.github.c2hy.clockworks.core.base;

import java.util.*;
import java.util.function.Supplier;

@Deprecated
public class BeanContainer {
    private final Map<Class<?>, List<Supplier<?>>> beanSuppliers = new HashMap<>();
    private final Map<Class<?>, List<Object>> beans = new HashMap<>();

    public <T> T getBean(Class<T> type) {
        this.initBeans(type);

        return Optional.ofNullable(beans.get(type))
                .map(list -> {
                    if (list.size() != 1) {
                        throw new IllegalArgumentException("No single bean found for type " + type.getName());
                    }
                    return list.getFirst();
                })
                .map(type::cast)
                .orElseThrow();
    }

    public <T> List<T> getBeans(Class<T> type) {
        this.initBeans(type);

        return Optional.ofNullable(beans.get(type))
                .map(list -> list.stream().map(type::cast).toList())
                .orElseThrow();
    }

    public List<?> getGenericsBeans(Class<?> type) {
        this.initBeans(type);

        return Optional.ofNullable(beans.get(type))
                .orElseThrow();
    }

    private void initBeans(Class<?> type) {
        List<?> objects = beans.get(type);
        if (objects != null) {
            return;
        }

        var suppliers = beanSuppliers.get(type);
        if (suppliers == null) {
            throw new IllegalArgumentException("No bean found for type " + type.getName());
        }

        beans.put(type, new ArrayList<>(suppliers.stream().map(Supplier::get).toList()));
    }

    public <T, E extends T> void register(Class<T> type, Supplier<E> supplier) {
        var objects = beanSuppliers.computeIfAbsent(type, k -> new ArrayList<>());
        objects.add(supplier);
    }

    public <T, E extends T> void registerBean(Class<T> type, E object) {
        beans.computeIfAbsent(type, k -> new ArrayList<>()).add(object);
    }
}
