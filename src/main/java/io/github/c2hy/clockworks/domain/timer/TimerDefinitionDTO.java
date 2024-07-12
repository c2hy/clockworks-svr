package io.github.c2hy.clockworks.domain.timer;

import lombok.Data;

@Data
public class TimerDefinitionDTO {
    private String id;
    private String name;
    private String description;
    private int initialDelaySeconds;
    private int intervalSeconds;
    private TimerTypeEnum type;
    private String callbackUrl;
}
