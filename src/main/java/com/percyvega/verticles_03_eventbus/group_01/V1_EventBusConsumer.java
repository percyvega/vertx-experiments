package com.percyvega.verticles_03_eventbus.group_01;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.LocalTime;

public class V1_EventBusConsumer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(V1_EventBusConsumer.class);

    public static void main(String[] args) {
        LOGGER.info("*********************************************************** Hello from " + V1_EventBusConsumer.class.getSimpleName());

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true);

        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if (resultHandler.succeeded()) {
                Vertx vertx = resultHandler.result();
                vertx.deployVerticle(new V1_EventBusConsumer());
            }
        });
    }

    @Override
    public void start() {
        LOGGER.info("*********************************************************** Verticle App Started ***********************************************************");

        vertx.eventBus().consumer("com.percyvega.vertx.eventbus", messageHandler -> {
            JsonObject toReply = new JsonObject().put("Answer", LocalTime.now().toString());
            LOGGER.info("Request: " + messageHandler.body() + " - To respond: " + toReply);
            messageHandler.reply(toReply);
        });
    }

    @Override
    public void stop() {
        LOGGER.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
