package com.percyvega.experiments.v3_inter_verticle_communication.g2_eventBus.g2_separateJvms_clustered.send;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.LocalTime;

public class SenderApp extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(SenderApp.class.getName());
    public static final String ADDRESS = "com.percyvega.vertx.eventbus";
    private int countMessages = 0;

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
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".start() ***********************************************************");

        vertx.setPeriodic(5000, handler -> sendMessageToBus());
    }

    private void sendMessageToBus() {
        JsonObject messageToSend = new JsonObject()
                .put("messageId", ++countMessages)
                .put("Question", "What time is it over there? Here it is " + LocalTime.now());

        log.info("Sending message: " + messageToSend);

        vertx.eventBus().send(ADDRESS, messageToSend, replyHandler -> {
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
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".stop() ***********************************************************");
    }
}
