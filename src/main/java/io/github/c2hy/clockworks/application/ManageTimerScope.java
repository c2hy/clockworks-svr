package io.github.c2hy.clockworks.application;

import io.github.c2hy.clockworks.domain.Transactions;
import io.github.c2hy.clockworks.domain.group.GroupService;
import io.github.c2hy.clockworks.domain.timer.TimerService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ManageTimerScope {
    private final Transactions transactions;
    private final TimerService timerService;
    private final GroupService groupService;

    public void createOrUpdateGroup(CreateOrUpdateGroupRequest request) {
        var group = groupService.createOrUpdateGroup(request.getGroup());
        groupService.save(group);
    }

    public void createOrUpdateTimer(CreateOrUpdateTimerRequest request) {
        var group = groupService.createOrUpdateGroup(request.getGroup());
        var timerDefinitions = timerService.createOrUpdateTimer(group, request.getTimers());

        transactions.execute(() -> {
            groupService.save(group);
            timerService.deleteGroupTimer(group.getId(), timerDefinitions);
            timerService.save(timerDefinitions);
        });
    }

    public void changeTimer(ChangeTimerRequest request) {
        var timer = timerService.changeTimer(request.getTimer());
        timerService.save(timer);
    }
}
