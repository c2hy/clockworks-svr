package io.github.c2hy.clockworks;

import io.github.c2hy.clockworks.application.*;
import io.github.c2hy.clockworks.domain.group.GroupService;
import io.github.c2hy.clockworks.domain.timer.TimerService;
import io.github.c2hy.clockworks.infrastructure.MockNotificationService;
import io.github.c2hy.clockworks.infrastructure.OkHttpNotificationService;
import io.github.c2hy.clockworks.infrastructure.RoutingDispatcher;
import io.github.c2hy.clockworks.infrastructure.repository.PgGroupRepository;
import io.github.c2hy.clockworks.infrastructure.repository.PgTimerRepository;
import io.github.c2hy.clockworks.infrastructure.repository.PgTranslations;

import static io.github.c2hy.clockworks.infrastructure.Configures.initConfiguration;
import static io.github.c2hy.clockworks.infrastructure.RoutingDispatcher.action;

public class ApplicationStater {
    public static void main(String[] args) {
        initConfiguration();

        var translations = new PgTranslations();
        var timerRepository = new PgTimerRepository();
        var notificationService = new OkHttpNotificationService();
        var timerService = new TimerService(timerRepository, notificationService);

        var groupRepository = new PgGroupRepository();
        var groupService = new GroupService(groupRepository);

        var manageTimerScope = new ManageTimerScope(translations, timerService, groupService);
        var scheduleTimerScope = new ScheduleTimerScope(timerService);

        action(
                "create-or-update-group",
                CreateOrUpdateGroupRequest.class,
                manageTimerScope::createOrUpdateGroup
        );
        action(
                "create-or-update-timer",
                CreateOrUpdateTimerRequest.class,
                manageTimerScope::createOrUpdateTimer
        );
        action(
                "change-timer",
                ChangeTimerRequest.class,
                manageTimerScope::changeTimer
        );
        action("ping", SelfPingScope::ping);

        RoutingDispatcher.startServer();
        scheduleTimerScope.fixedTimeTrigger();
    }
}
