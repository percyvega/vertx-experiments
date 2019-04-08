package com.percyvega.v3_inter_verticle_communication.g2_eventBus.g2_separateJvms_clustered.send.when_errors_happen;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SenderHopefulApp extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(SenderHopefulApp.class.getName());

    private static int counter = 0;

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + SenderHopefulApp.class.getSimpleName());

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true);

        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if(resultHandler.succeeded()) {
                Vertx vertx = resultHandler.result();
                vertx.deployVerticle(new SenderHopefulApp());
            }
        });
    }

    @Override
    public void start() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".start() ***********************************************************");

        vertx.setPeriodic(5000, handler -> sendMessageToBus());
    }

    private void sendMessageToBus() {
        String messageToSend = "Percy " + ++counter;

        log.info("+++++++++++++++++++++++++++++ Sending message: " + messageToSend);

        vertx.eventBus().send("com.percyvega.vertx.eventbus.hello", messageToSend, replyHandler -> {
            if(replyHandler.succeeded()) {
                JsonObject reply = (JsonObject) replyHandler.result().body();
                log.info("+++++++++++++++++++++++++++++ Received reply: " + reply);
            } else {
                log.error("+++++++++++++++++++++++++++++ replyHandler failed");
            }
        });
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".stop() ***********************************************************");
    }
}
