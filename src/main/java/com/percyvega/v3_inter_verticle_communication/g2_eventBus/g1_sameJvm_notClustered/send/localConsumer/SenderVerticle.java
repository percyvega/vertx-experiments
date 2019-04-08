package com.percyvega.v3_inter_verticle_communication.g2_eventBus.g1_sameJvm_notClustered.send.localConsumer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.LocalTime;

public class SenderVerticle extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(SenderVerticle.class.getName());
    public static final String ADDRESS = "eventBus.MainApp";
    private int countMessages = 0;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new LocalConsumer1Verticle(), res -> {
            if(res.succeeded()){
                log.info("Deployment id is: " + res.result());
            } else {
                log.error("Deployment failed!");
            }
        });

        vertx.deployVerticle(new LocalConsumer2Verticle(), res -> {
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
