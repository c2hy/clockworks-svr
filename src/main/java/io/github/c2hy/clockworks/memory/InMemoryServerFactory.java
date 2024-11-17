package io.github.c2hy.clockworks.memory;

import io.github.c2hy.clockworks.core.common.CallbackInvoker;
import io.github.c2hy.clockworks.core.definition.TimerDefinitionRepository;
import io.github.c2hy.clockworks.core.operation.ServerFactory;
import io.github.c2hy.clockworks.core.operation.TimerOperationRecordRepository;
import io.github.c2hy.clockworks.core.timer.TimerRepository;
import io.github.c2hy.clockworks.core.timer.TimerScheduler;

public class InMemoryServerFactory extends ServerFactory {
    private final InMemoryTimerOperationRecordRepository timerOperationRecordRepository = new InMemoryTimerOperationRecordRepository();
    private final InMemoryTimerDefinitionRepository timerDefinitionRepository = new InMemoryTimerDefinitionRepository();
    private final InMemoryTimerRepository timerRepository = new InMemoryTimerRepository();
    private final InMemoryTimerScheduler timerScheduler = new InMemoryTimerScheduler(timerRepository);
    private final DoNothingCallbackInvoker callbackInvoker = new DoNothingCallbackInvoker();

    @Override
    protected TimerOperationRecordRepository createTimerOperationRecordRepository() {
        return this.timerOperationRecordRepository;
    }

    @Override
    protected TimerDefinitionRepository createTimerDefinitionRepository() {
        return this.timerDefinitionRepository;
    }

    @Override
    protected TimerRepository createTimerRepository() {
        return this.timerRepository;
    }

    @Override
    protected TimerScheduler createTimerScheduler() {
        return this.timerScheduler;
    }

    @Override
    protected CallbackInvoker createCallbackInvoker() {
        return this.callbackInvoker;
    }
}
