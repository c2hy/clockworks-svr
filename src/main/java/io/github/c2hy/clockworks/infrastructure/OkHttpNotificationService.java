package io.github.c2hy.clockworks.infrastructure;

import io.github.c2hy.clockworks.domain.timer.NotificationService;
import lombok.SneakyThrows;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OkHttpNotificationService implements NotificationService {
    private final Logger logger = LoggerFactory.getLogger(OkHttpNotificationService.class);
    private final OkHttpClient client = new OkHttpClient();

    @SneakyThrows
    @Override
    public void sendNotification(boolean syncSend, String url, Object body) {
        var newCall = client.newCall(new Request.Builder()
                .url(url)
                .get().build());
        if (syncSend) {
            try (var response = newCall.execute()) {
                logger.info("send {}", response.isSuccessful());
            }
        } else {
            newCall.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    logger.info("send notification failed: {}", e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    logger.info("send notification success: {}", response.message());
                }
            });
        }
    }
}
