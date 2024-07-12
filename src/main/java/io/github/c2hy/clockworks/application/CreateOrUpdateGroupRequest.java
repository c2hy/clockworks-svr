package io.github.c2hy.clockworks.application;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.github.c2hy.clockworks.domain.group.GroupDTO;
import lombok.Data;

@Data
public class CreateOrUpdateGroupRequest {
    @JsonUnwrapped
    private GroupDTO group;
}
