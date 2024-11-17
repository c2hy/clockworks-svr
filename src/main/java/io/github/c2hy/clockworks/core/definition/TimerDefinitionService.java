package io.github.c2hy.clockworks.core.definition;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.c2hy.clockworks.core.common.CronUtils;
import io.github.c2hy.clockworks.core.common.ObjectUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class TimerDefinitionService {
    private final TimerDefinitionRepository timerDefinitionRepository;

    public String deleteTimerDefinition(String groupId, JsonNode jsonNode) {
        var key = jsonNode.asText();
        return this.deleteTimerDefinition(groupId, key);
    }

    @Nullable
    public String deleteTimerDefinition(String groupId, String key) {
        return timerDefinitionRepository.findByGroupIdAndKey(groupId, key)
                .filter(v -> {
                    var deleted = timerDefinitionRepository.deleteById(v.id());
                    if (deleted) {
                        log.debug("{} timer definition deleted", v.id());
                    }
                    return deleted;
                })
                .map(TimerDefinition::id)
                .orElse(null);
    }

    public TimerDefinitionSaveResult saveTimerDefinition(String groupId, JsonNode jsonNode) {
        var key = jsonNode.path("key").asText();
        if (ObjectUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key is empty");
        }

        var cronExpression = jsonNode.path("cronExpression").asText();
        if (CronUtils.isInvalidExpression(cronExpression)) {
            throw new IllegalArgumentException("cronExpression is invalid");
        }

        var callbackRouting = jsonNode.path("callbackRouting").asText();
        if (ObjectUtils.isEmpty(callbackRouting)) {
            throw new IllegalArgumentException("callbackRouting is empty");
        }

        var beginAt = Optional.ofNullable(jsonNode.get("beginAt"))
                .map(JsonNode::asText)
                .map(Instant::parse)
                .orElse(null);
        var endAt = Optional.ofNullable(jsonNode.get("endAt"))
                .map(JsonNode::asText)
                .map(Instant::parse)
                .orElse(null);

        if (beginAt != null && endAt != null && beginAt.isAfter(endAt)) {
            throw new IllegalArgumentException("beginAt is after endAt");
        }

        var existedId = timerDefinitionRepository.findByGroupIdAndKey(groupId, key)
                .map(TimerDefinition::id)
                .orElse(null);

        var timerDefinition = new TimerDefinition(
                UUID.randomUUID().toString(),
                groupId,
                key,
                callbackRouting,
                cronExpression,
                beginAt,
                endAt
        );

        timerDefinition = timerDefinitionRepository.save(timerDefinition);

        var saveResult = new TimerDefinitionSaveResult();
        saveResult.setDeleteDefinitionId(existedId);
        saveResult.setNewDefinition(timerDefinition);
        return saveResult;
    }
}
