package io.github.c2hy.clockworks.core.operation;

import lombok.Data;

@Data
public class TimerOperationResponse {
    private boolean executed;

    public static TimerOperationResponse executed() {
        var timerOperationResponse = new TimerOperationResponse();
        timerOperationResponse.setExecuted(true);
        return timerOperationResponse;
    }

    public static TimerOperationResponse ignored() {
        var timerOperationResponse = new TimerOperationResponse();
        timerOperationResponse.setExecuted(false);
        return timerOperationResponse;
    }
}
