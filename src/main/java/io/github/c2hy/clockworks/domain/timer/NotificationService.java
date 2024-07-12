package io.github.c2hy.clockworks.domain.timer;

public interface NotificationService {
    void sendNotification(boolean syncSend, String url, Object body);
}
