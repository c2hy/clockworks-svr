package io.github.c2hy.clockworks.core.base;

import io.github.c2hy.clockworks.core.common.JsonUtils;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class Undertows {
    private final Logger logger = LoggerFactory.getLogger("INIT");
    private final RoutingHandler routingHandler = new RoutingHandler();

    public void start() {
        int httpPort = Integer.parseInt(System.getProperty("HTTP_PORT"));
        var server = Undertow.builder()
                .addHttpListener(httpPort, "localhost")
                .setHandler(routingHandler)
                .build();
        server.start();
        logger.info("clockworks started on {}", httpPort);
    }

    public void action(String actionCode, Runnable runnable) {
        routingHandler.add("GET", "/actions/" + actionCode, exchange ->
                this.handleError(exchange, () -> {
                    runnable.run();
                    this.responseEmpty(exchange);
                }));
    }

    public void action(String actionCode, Supplier<?> supplier) {
        routingHandler.add("GET", "/actions/" + actionCode, exchange ->
                this.handleError(exchange, () -> this.responseJson(exchange, supplier.get()))
        );
    }

    public <T> void action(String actionCode, Class<T> tClass, Consumer<T> consumer) {
        routingHandler.add("POST", "/actions/" + actionCode, exchange ->
                exchange.getRequestReceiver().receiveFullBytes((httpServerExchange, bytes) ->
                        this.handleError(exchange, () -> {
                            consumer.accept(JsonUtils.fromJson(bytes, tClass));
                            this.responseEmpty(exchange);
                        })
                )
        );
    }

    public <T> void action(String actionCode, Class<T> tClass, Function<T, ?> function) {
        routingHandler.add("POST", "/actions/" + actionCode, exchange ->
                exchange.getRequestReceiver().receiveFullBytes((httpServerExchange, bytes) ->
                        this.handleError(exchange, () ->
                                this.responseJson(
                                        exchange,
                                        function.apply(JsonUtils.fromJson(bytes, tClass))
                                ))
                )
        );
    }

    private void handleError(HttpServerExchange exchange, Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            this.responseError(exchange, e);
        }
    }

    private void responseJson(HttpServerExchange exchange, Object o) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(JsonUtils.toJson(o));
    }

    private void responseEmpty(HttpServerExchange exchange) {
        exchange.setStatusCode(StatusCodes.NO_CONTENT);
        exchange.getResponseSender().close();
    }

    private void responseError(HttpServerExchange exchange, Exception exception) {
        exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
        exchange.getResponseSender().send(String.format("""
                "error": "%s"
                """, exception.getMessage()));
    }
}
