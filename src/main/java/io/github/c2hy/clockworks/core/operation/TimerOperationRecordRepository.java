package io.github.c2hy.clockworks.core.operation;

import java.util.Optional;

public interface TimerOperationRecordRepository {
    Optional<TimerOperationRecord> lastOperation(String groupId);

    boolean save(TimerOperationRecord timerOperationRecord);
}
