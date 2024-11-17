package io.github.c2hy.clockworks.memory;

import io.github.c2hy.clockworks.core.operation.TimerOperationRecord;
import io.github.c2hy.clockworks.core.operation.TimerOperationRecordRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTimerOperationRecordRepository implements TimerOperationRecordRepository {
    private final Map<String, TimerOperationRecord> store = new ConcurrentHashMap<>();

    @Override
    public Optional<TimerOperationRecord> lastOperation(String groupId) {
        return Optional.ofNullable(store.get(groupId));
    }

    @Override
    public boolean save(TimerOperationRecord timerOperationRecord) {
        store.put(timerOperationRecord.getGroupId(), timerOperationRecord);
        return true;
    }
}
