package io.github.c2hy.clockworks.infrastructure.utils;

public final class Checking {
    public static void exceptOrThrow(Boolean except, String checkMessage) throws CheckingException {
        if (!except) {
            throw new CheckingException(checkMessage);
        }
    }
}
