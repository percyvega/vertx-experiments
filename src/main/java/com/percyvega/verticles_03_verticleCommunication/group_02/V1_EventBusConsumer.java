package com.percyvega.verticles_03_verticleCommunication.group_02;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.LocalTime;

public class V1_EventBusConsumer extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(V1_EventBusConsumer.class);

    public static void main(String[] args) {
        log.info("*********************************************************** Hello from " + V1_EventBusConsumer.class.getSimpleName());

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
        log.info("*********************************************************** Verticle App Started ***********************************************************");

        vertx.eventBus().consumer("com.percyvega.vertx.eventbus", messageHandler -> {
            JsonObject toReply = new JsonObject().put("Answer", LocalTime.now().toString());
            log.info("Request: " + messageHandler.body() + " - To respond: " + toReply);
            messageHandler.reply(toReply);
        });
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
