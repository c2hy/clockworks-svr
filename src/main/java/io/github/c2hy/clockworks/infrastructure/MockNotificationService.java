package io.github.c2hy.clockworks.infrastructure;

import io.github.c2hy.clockworks.domain.timer.NotificationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockNotificationService implements NotificationService {
    @Override
    public void sendNotification(boolean syncSend, String url, Object body) {
        log.info("trigger {} {}", url, body);
    }
}
