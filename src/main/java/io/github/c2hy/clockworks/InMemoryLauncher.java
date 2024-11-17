package io.github.c2hy.clockworks;

import io.github.c2hy.clockworks.core.base.Configures;
import io.github.c2hy.clockworks.memory.InMemoryServerFactory;

public class InMemoryLauncher {
    public static void main(String[] args) {
        Configures.initConfiguration();
        new InMemoryServerFactory()
                .createServer()
                .start();
    }
}
