package com.percyvega.verticles_03_verticleCommunication.group_03;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class V2_HopefulSender extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(V2_HopefulSender.class);

    private static int counter = 0;

    public static void main(String[] args) {
        log.info("*********************************************************** Hello from " + V2_HopefulSender.class.getSimpleName());

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
        log.info("*********************************************************** Verticle App Started ***********************************************************");

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
        log.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
