package com.percyvega.v3_inter_verticle_communication.g2_SameJvm_EventBus_NotClustered;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Date;

public class EventBusSameJvm {
    private static final Logger log = LoggerFactory.getLogger(EventBusSameJvm.class);

    public static final String EVENT_BUS_ADDRESS = "eventBus.EventBusSameJvm";

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new ConsumerVerticle(), res -> {
            if(res.succeeded()){
                log.info("Deployment id is: " + res.result());
            } else {
                log.error("Deployment failed!");
            }
        });

        vertx.deployVerticle(new SenderVerticle(), res -> {
            if(res.succeeded()){
                log.info("Deployment id is: " + res.result());
            } else {
                log.error("Deployment failed!");
            }
        });
    }
}

class SenderVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(SenderVerticle.class);

    @Override
    public void start() {
        vertx.setPeriodic(1000, handler -> sendMessageToBus());
    }

    private void sendMessageToBus() {
        String message = "Sending a message at " + new Date(System.currentTimeMillis());
        vertx.eventBus().send(EventBusSameJvm.EVENT_BUS_ADDRESS, message, asyncResult -> {
            if(asyncResult.succeeded()) {
                log.info("Received this reply: " + asyncResult.result().body());
            } else {
                log.error("No reply");
            }
        });
    }
}

class ConsumerVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(ConsumerVerticle.class);

    @Override
    public void start() {
        vertx.eventBus().consumer(EventBusSameJvm.EVENT_BUS_ADDRESS, message -> {
            log.info("Received this message: " + message.body());
            String reply = "Replying at " + new Date(System.currentTimeMillis());
            message.reply(reply);
        });
    }
}
