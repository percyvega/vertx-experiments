package com.percyvega.verticle_group_06;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class V2_EventBus_Sender extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(V2_EventBus_Sender.class);

    public static void main(String[] args) {
        LOGGER.info("Hello from " + V2_EventBus_Sender.class.getSimpleName());

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true);

        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if(resultHandler.succeeded()) {
                Vertx vertx = resultHandler.result();
                vertx.deployVerticle(new V2_EventBus_Sender());
            }
        });
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("Verticle App Started");

        vertx.setPeriodic(5000, handler -> sendMessageToBus());
    }

    private void sendMessageToBus() {
        JsonObject messageToSend = new JsonObject();
        messageToSend.put("Question", "What time is it?");

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
    public void stop() throws Exception {
        LOGGER.info("Verticle App Stopped");
    }
}
