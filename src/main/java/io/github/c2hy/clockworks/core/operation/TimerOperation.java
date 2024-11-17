package io.github.c2hy.clockworks.core.operation;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class TimerOperation {
    private String operation;
    private JsonNode content;
}
