package com.percyvega.verticles_03_eventbus.group_01;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.LocalTime;

public class V2_EventBusSender extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(V2_EventBusSender.class);

    public static void main(String[] args) {
        LOGGER.info("*********************************************************** Hello from " + V2_EventBusSender.class.getSimpleName());

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true);

        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if(resultHandler.succeeded()) {
                Vertx vertx = resultHandler.result();
                vertx.deployVerticle(new V2_EventBusSender());
            }
        });
    }

    @Override
    public void start() {
        LOGGER.info("*********************************************************** Verticle App Started ***********************************************************");

        vertx.setPeriodic(5000, handler -> sendMessageToBus());
    }

    private void sendMessageToBus() {
        JsonObject messageToSend = new JsonObject();
        messageToSend.put("Question", "What time is it over there? Here it is " + LocalTime.now());

        LOGGER.info("Sending message: " + messageToSend);

        vertx.eventBus().send("com.percyvega.vertx.eventbus", messageToSend, replyHandler -> {
            if(replyHandler.succeeded()) {
                JsonObject reply = (JsonObject) replyHandler.result().body();
                LOGGER.info("Received reply: " + reply);
            } else {
                LOGGER.error("replyHandler failed");
            }
        });
    }

    @Override
    public void stop() {
        LOGGER.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
