package com.percyvega.v3_inter_verticle_communication.g3_SeparateJvms_EventBus_Clustered;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.LocalTime;

public class SenderApp extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(SenderApp.class);

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + SenderApp.class.getSimpleName());

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true);

        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if(resultHandler.succeeded()) {
                Vertx vertx = resultHandler.result();
                vertx.deployVerticle(new SenderApp());
            }
        });
    }

    @Override
    public void start() {
        log.info("*********************************************************** Verticle App Started ***********************************************************");

        vertx.setPeriodic(5000, handler -> sendMessageToBus());
    }

    private void sendMessageToBus() {
        JsonObject messageToSend = new JsonObject();
        messageToSend.put("Question", "What time is it over there? Here it is " + LocalTime.now());

        log.info("Sending message: " + messageToSend);

        vertx.eventBus().send("com.percyvega.vertx.eventbus", messageToSend, replyHandler -> {
            if(replyHandler.succeeded()) {
                JsonObject reply = (JsonObject) replyHandler.result().body();
                log.info("Received reply: " + reply);
            } else {
                log.error("replyHandler failed");
            }
        });
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
