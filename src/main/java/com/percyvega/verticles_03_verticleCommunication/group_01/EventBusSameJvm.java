package com.percyvega.verticles_03_verticleCommunication.group_01;

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
        Date date = new Date(System.currentTimeMillis());
        vertx.eventBus().send(EventBusSameJvm.EVENT_BUS_ADDRESS, date.toString());
    }
}

class ConsumerVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(ConsumerVerticle.class);

    @Override
    public void start() {
        vertx.eventBus().consumer(EventBusSameJvm.EVENT_BUS_ADDRESS, (message) -> {
            log.info(message.body().toString());
        });
    }
}
