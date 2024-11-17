package io.github.c2hy.clockworks.core.operation;

import io.github.c2hy.clockworks.core.common.ObjectUtils;
import io.github.c2hy.clockworks.core.definition.TimerDefinition;
import io.github.c2hy.clockworks.core.definition.TimerDefinitionService;
import io.github.c2hy.clockworks.core.timer.TimerService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
public class TimerOperationUseCase {
    private final TimerOperationRecordRepository timerOperationRecordRepository;
    private final TimerDefinitionService timerDefinitionService;
    private final TimerService timerService;

    public TimerOperationResponse handleOperation(TimerOperationRequest request) {
        log.info("handle operation request {}", request.getGroupId());

        var groupId = request.getGroupId();
        if (ObjectUtils.isEmpty(groupId)) {
            throw new IllegalArgumentException("groupId is empty");
        }
        var operations = request.getOperations();
        if (ObjectUtils.isEmpty(operations)) {
            throw new IllegalArgumentException("operations is empty");
        }

        var lastOperationOptional = timerOperationRecordRepository.lastOperation(groupId);

        var latestOperationTime = lastOperationOptional
                .map(TimerOperationRecord::getOperationTime)
                .orElse(null);
        var cooldownSeconds = request.getCooldownSeconds();
        if (cooldownSeconds > 0 && this.isCooldown(latestOperationTime, cooldownSeconds)) {
            log.info("cooldown {} seconds", cooldownSeconds);
            return TimerOperationResponse.ignored();
        }

        var timerOperationRecord = new TimerOperationRecord();
        timerOperationRecord.setGroupId(groupId);
        timerOperationRecord.setOperationTime(Instant.now());
        timerOperationRecord.setVersion(lastOperationOptional
                .map(TimerOperationRecord::getVersion)
                .orElse(0));
        var saved = timerOperationRecordRepository.save(timerOperationRecord);
        if (!saved) {
            log.info("save timer operation record failed");
            return TimerOperationResponse.ignored();
        }

        var newTimerDefinitions = new ArrayList<TimerDefinition>();
        var deletedTimerDefinitionIds = new ArrayList<String>();
        for (var operation : request.getOperations()) {
            switch (operation.getOperation()) {
                case TimerOperationConstant.DELETE -> {
                    var id = timerDefinitionService.deleteTimerDefinition(groupId, operation.getContent());
                    if (id != null) {
                        deletedTimerDefinitionIds.add(id);
                    }
                }
                case TimerOperationConstant.SAVE -> {
                    var saveResult = timerDefinitionService.saveTimerDefinition(groupId, operation.getContent());
                    newTimerDefinitions.add(saveResult.getNewDefinition());
                    if (saveResult.getDeleteDefinitionId() != null) {
                        deletedTimerDefinitionIds.add(saveResult.getDeleteDefinitionId());
                    }
                }
                default -> {
                    var errorMessage = "unknown operation " + operation.getOperation();
                    log.error(errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }
            }
        }

        timerService.deleteByTimerDefinitionIds(deletedTimerDefinitionIds);
        timerService.createByTimerDefinition(newTimerDefinitions);

        return TimerOperationResponse.executed();
    }

    private boolean isCooldown(@Nullable Instant lastOperationTime,
                               int cooldownSeconds) {
        if (lastOperationTime == null) {
            return false;
        }

        var seconds = Duration.between(lastOperationTime, Instant.now()).toSeconds();
        return seconds < cooldownSeconds;
    }
}
