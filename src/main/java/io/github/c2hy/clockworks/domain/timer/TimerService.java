package io.github.c2hy.clockworks.domain.timer;

import io.github.c2hy.clockworks.domain.group.Group;
import io.github.c2hy.clockworks.infrastructure.utils.Checking;
import io.github.c2hy.clockworks.infrastructure.utils.CheckingException;
import io.github.c2hy.clockworks.infrastructure.utils.ObjectUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@ExtensionMethod({Checking.class})
@RequiredArgsConstructor
@Slf4j
public class TimerService {
    private final TimerRepository timerRepository;
    private final NotificationService notificationService;

    public List<TimerDefinition> createOrUpdateTimer(Group group, List<TimerDefinitionDTO> timerDefinitions) {
        if (group.ignoreUpdate()) {
            log.debug("{} updating too frequently", group.getId());
            return Collections.emptyList();
        }

        var definitionIds = timerDefinitions.stream().map(TimerDefinitionDTO::getId).collect(Collectors.toSet());
        "id duplication".trueOrThrow(Objects.equals(definitionIds.size(), timerDefinitions.size()));

        log.debug("create or update timer {}", definitionIds);

        var timerMap = timerRepository.findAllById(definitionIds)
                .stream()
                .collect(Collectors.toMap(TimerDefinition::getId, Function.identity()));

        return timerDefinitions.stream().map(timerDefinitionDTO -> {
            var timerDefinition = timerMap.get(timerDefinitionDTO.getId());
            return this.doCreateOrUpdateTimer(group.getId(), timerDefinition, timerDefinitionDTO);
        }).toList();
    }

    private TimerDefinition doCreateOrUpdateTimer(String groupId,
                                                  @Nullable TimerDefinition existedTimerDefinition,
                                                  TimerDefinitionDTO timerDefinitionDTO) {
        if (ObjectUtils.isEmpty(groupId)) {
            log.debug("{} is no group timer", timerDefinitionDTO.getId());
        }

        var timerDefinition = TimerDefinition.merge(timerDefinitionDTO, existedTimerDefinition, groupId);
        timerDefinition.check();

        return timerDefinition;
    }

    public TimerDefinition changeTimer(TimerDefinitionDTO timerDefinitionDTO) {
        var existedTimerDefinition = timerRepository.findById(timerDefinitionDTO.getId())
                .orElseThrow(() -> new CheckingException("timer not found"));

        log.info("change timer {}", existedTimerDefinition.getId());

        return TimerDefinition.merge(timerDefinitionDTO, existedTimerDefinition, null);
    }

    public void save(Collection<TimerDefinition> timerDefinitions) {
        if (timerDefinitions.isEmpty()) {
            return;
        }

        var timers = timerDefinitions.stream()
                .map(TimerDefinition::firstTimer)
                .toList();
        timerRepository.save(timerDefinitions);
        timerRepository.createTimer(timers);
    }

    public void save(TimerDefinition timerDefinition) {
        var timer = timerDefinition.firstTimer();
        timerRepository.save(timerDefinition);
        timerRepository.deleteByDefinitionId(timerDefinition.getId());
        timerRepository.createTimer(timer);
    }

    public Collection<Integer> loadRunningTimerIds() {
        return timerRepository.loadRunningTimerIds();
    }

    public void triggerTimer(Integer timerId) {
        var timer = timerRepository.lockTimer(timerId);
        if (timer == null) {
            log.debug("{} timer canceled", timerId);
            return;
        }

        var timerDefinition = timerRepository.findById(timer.getDefinitionId())
                .orElse(null);

        if (timerDefinition == null) {
            log.debug("{} timer definition deleted", timer.getDefinitionId());
            return;
        }

        timer.notice(notificationService);

        var nextTimer = timerDefinition.nextTimer();
        if (nextTimer != null) {
            timerRepository.createTimer(nextTimer);
        }
    }

    public void deleteGroupTimer(String groupId, List<TimerDefinition> timerDefinitions) {
        timerRepository.deleteByGroupId(groupId, timerDefinitions.stream().map(TimerDefinition::getId).toList());
    }
}
