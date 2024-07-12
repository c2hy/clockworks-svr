package io.github.c2hy.clockworks.infrastructure.repository;

import io.github.c2hy.clockworks.domain.Transactions;

public class PgTranslations implements Transactions {
    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
