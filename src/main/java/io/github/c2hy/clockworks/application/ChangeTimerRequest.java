package io.github.c2hy.clockworks.application;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.github.c2hy.clockworks.domain.timer.TimerDefinitionDTO;
import lombok.Data;

@Data
public class ChangeTimerRequest {
    @JsonUnwrapped
    private TimerDefinitionDTO timer;
}
