package io.github.c2hy.clockworks.infrastructure;

import io.github.c2hy.clockworks.infrastructure.utils.Jsons;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;
import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;

public class RoutingDispatcher {
    private static final Logger logger = getLogger("INIT");
    private static final RoutingHandler ROUTING_HANDLER = new RoutingHandler();

    public static void startServer() {
        int httpPort = Integer.parseInt(System.getProperty("HTTP_PORT"));
        var server = Undertow.builder()
                .addHttpListener(httpPort, "localhost")
                .setHandler(RoutingDispatcher.ROUTING_HANDLER)
                .build();
        server.start();
        logger.info("clockworks started on {}", httpPort);
    }

    public static void action(String actionCode, Runnable runnable) {
        ROUTING_HANDLER.add("GET", "/actions/" + actionCode, exchange -> {
            handleError(exchange, () -> {
                runnable.run();
                responseEmpty(exchange);
            });
        });
    }

    public static void action(String actionCode, Supplier<?> supplier) {
        ROUTING_HANDLER.add("GET", "/actions/" + actionCode, exchange ->
                handleError(exchange, () -> responseJson(exchange, supplier.get()))
        );
    }

    public static <T> void action(String actionCode, Class<T> tClass, Consumer<T> consumer) {
        ROUTING_HANDLER.add("POST", "/actions/" + actionCode, exchange ->
                exchange.getRequestReceiver().receiveFullBytes((httpServerExchange, bytes) ->
                        handleError(exchange, () -> {
                            consumer.accept(Jsons.fromJson(bytes, tClass));
                            responseEmpty(exchange);
                        })
                )
        );
    }

    public static <T> void action(String actionCode, Class<T> tClass, Function<T, ?> function) {
        ROUTING_HANDLER.add("POST", "/actions/" + actionCode, exchange ->
                exchange.getRequestReceiver().receiveFullBytes((httpServerExchange, bytes) ->
                        handleError(exchange, () -> responseJson(exchange, function.apply(Jsons.fromJson(bytes, tClass))))
                )
        );
    }

    private static void handleError(HttpServerExchange exchange, Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            responseError(exchange, e);
        }
    }

    private static void responseJson(HttpServerExchange exchange, Object o) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(Jsons.toJson(o));
    }

    private static void responseEmpty(HttpServerExchange exchange) {
        exchange.setStatusCode(204);
        exchange.getResponseSender().close();
    }

    public static void responseError(HttpServerExchange exchange, Exception exception) {
        exchange.setStatusCode(500);
        exchange.getResponseSender().send(String.format("""
                "error": "%s"
                """, exception.getMessage()));
    }
}
