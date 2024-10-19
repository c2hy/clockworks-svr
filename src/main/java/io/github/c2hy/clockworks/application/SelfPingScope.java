package io.github.c2hy.clockworks.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfPingScope {
    private static final Logger logger = LoggerFactory.getLogger(SelfPingScope.class);

    public static void ping() {
        logger.info("Ping");
    }
}
