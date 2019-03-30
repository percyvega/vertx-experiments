package com.percyvega.verticles_03_eventbus.group_02;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.LocalTime;

public class V2_HopefulSender extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(V2_HopefulSender.class);

    private static int counter = 0;

    public static void main(String[] args) {
        LOGGER.info("*********************************************************** Hello from " + V2_HopefulSender.class.getSimpleName());

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true);

        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if(resultHandler.succeeded()) {
                Vertx vertx = resultHandler.result();
                vertx.deployVerticle(new V2_HopefulSender());
            }
        });
    }

    @Override
    public void start() {
        LOGGER.info("*********************************************************** Verticle App Started ***********************************************************");

        vertx.setPeriodic(5000, handler -> sendMessageToBus());
    }

    private void sendMessageToBus() {
        String messageToSend = "Percy " + ++counter;

        LOGGER.info("+++++++++++++++++++++++++++++ Sending message: " + messageToSend);

        vertx.eventBus().send("com.percyvega.vertx.eventbus.hello", messageToSend, replyHandler -> {
            if(replyHandler.succeeded()) {
                JsonObject reply = (JsonObject) replyHandler.result().body();
                LOGGER.info("+++++++++++++++++++++++++++++ Received reply: " + reply);
            } else {
                LOGGER.error("+++++++++++++++++++++++++++++ replyHandler failed");
            }
        });
    }

    @Override
    public void stop() {
        LOGGER.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
