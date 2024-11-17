package io.github.c2hy.clockworks.core.operation;

import lombok.Data;

import java.util.List;

@Data
public class TimerOperationRequest {
    private String groupId;
    private int cooldownSeconds;
    private List<TimerOperation> operations;
}
