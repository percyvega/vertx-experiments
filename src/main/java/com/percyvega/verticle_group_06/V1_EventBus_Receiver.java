package com.percyvega.verticle_group_06;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.LocalTime;

public class V1_EventBus_Receiver extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(V1_EventBus_Receiver.class);

    public static void main(String[] args) {
        LOGGER.info("Hello from " + V1_EventBus_Receiver.class.getSimpleName());

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true);

        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if(resultHandler.succeeded()) {
                Vertx vertx = resultHandler.result();
                vertx.deployVerticle(new V1_EventBus_Receiver());
            }
        });
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("Verticle App Started");

        vertx.eventBus().consumer("com.percyvega.vertx.eventbus", messageHandler -> {
            JsonObject toReply = new JsonObject().put("Answer", LocalTime.now().toString());
            LOGGER.info("Request: " + messageHandler.body() + " - To respond: " + toReply);
            messageHandler.reply(toReply);
        });
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Verticle App Stopped");
    }
}
