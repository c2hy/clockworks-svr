package io.github.c2hy.clockworks.core.base;

import io.github.cdimascio.dotenv.Dotenv;

public class Configures {
    public static final String CLOCKWORKS_DB_URL = "CLOCKWORKS_DB_URL";
    public static final String CLOCKWORKS_DB_USER = "CLOCKWORKS_DB_USER";
    public static final String CLOCKWORKS_DB_PASSWORD = "CLOCKWORKS_DB_PASSWORD";
    public static final String CLOCKWORKS_DB_CACHE_PREP_STMTS = "CLOCKWORKS_DB_CACHE_PREP_STMTS";
    public static final String CLOCKWORKS_DB_PREP_STMT_CACHE_SIZE = "CLOCKWORKS_DB_PREP_STMT_CACHE_SIZE";
    public static final String CLOCKWORKS_DB_PREP_STMT_CACHE_SQL_LIMIT = "CLOCKWORKS_DB_PREP_STMT_CACHE_SQL_LIMIT";

    public static void initConfiguration() {
        Dotenv.configure().systemProperties().load();
    }
}
