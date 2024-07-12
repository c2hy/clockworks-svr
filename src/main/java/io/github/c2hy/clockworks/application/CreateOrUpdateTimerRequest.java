package io.github.c2hy.clockworks.application;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.github.c2hy.clockworks.domain.group.GroupDTO;
import io.github.c2hy.clockworks.domain.timer.TimerDefinitionDTO;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrUpdateTimerRequest {
    @JsonUnwrapped
    private GroupDTO group;
    private List<TimerDefinitionDTO> timers;
}
