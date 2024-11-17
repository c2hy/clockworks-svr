package io.github.c2hy.clockworks.core.operation;

import io.github.c2hy.clockworks.core.base.ControllerRegister;
import io.github.c2hy.clockworks.core.base.Undertows;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TimerOperationController implements ControllerRegister {
    private final TimerOperationUseCase timerOperationUseCase;

    @Override
    public void registerControllers(Undertows undertows) {
        undertows.action(
                "submit-operation",
                TimerOperationRequest.class,
                timerOperationUseCase::handleOperation
        );
    }
}
