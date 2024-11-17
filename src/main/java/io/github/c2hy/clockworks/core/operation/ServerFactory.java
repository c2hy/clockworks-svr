package io.github.c2hy.clockworks.core.operation;

import io.github.c2hy.clockworks.core.base.Undertows;
import io.github.c2hy.clockworks.core.common.CallbackInvoker;
import io.github.c2hy.clockworks.core.definition.TimerDefinitionRepository;
import io.github.c2hy.clockworks.core.definition.TimerDefinitionService;
import io.github.c2hy.clockworks.core.timer.TimerRepository;
import io.github.c2hy.clockworks.core.timer.TimerScheduler;
import io.github.c2hy.clockworks.core.timer.TimerService;
import io.github.c2hy.clockworks.core.timer.TimerTriggerUseCase;

public abstract class ServerFactory {
    protected abstract TimerOperationRecordRepository createTimerOperationRecordRepository();

    protected abstract TimerDefinitionRepository createTimerDefinitionRepository();

    protected abstract TimerRepository createTimerRepository();

    protected abstract TimerScheduler createTimerScheduler();

    protected abstract CallbackInvoker createCallbackInvoker();

    public Undertows createServer() {
        var undertows = new Undertows();

        new TimerOperationController(new TimerOperationUseCase(
                this.createTimerOperationRecordRepository(),
                new TimerDefinitionService(
                        this.createTimerDefinitionRepository()
                ),
                new TimerService(
                        this.createTimerRepository()
                )
        )).registerControllers(undertows);

        createTimerScheduler()
                .start(new TimerTriggerUseCase(
                        this.createCallbackInvoker(),
                        this.createTimerRepository()
                ));

        return undertows;
    }
}
