package io.github.c2hy.clockworks.core.operation;

import lombok.Data;

import java.time.Instant;

@Data
public class TimerOperationRecord {
    private String id;
    private String groupId;
    private Instant operationTime;
    private int version;
}
