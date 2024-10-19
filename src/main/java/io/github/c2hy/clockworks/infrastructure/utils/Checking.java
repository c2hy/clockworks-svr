package io.github.c2hy.clockworks.infrastructure.utils;

public final class Checking {
    public static void trueOrThrow(String checkMessage, boolean except) {
        if (!except) {
            throw new CheckingException(checkMessage);
        }
    }
}
