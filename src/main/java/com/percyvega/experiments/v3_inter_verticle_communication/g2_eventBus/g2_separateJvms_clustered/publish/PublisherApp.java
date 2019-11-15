package com.percyvega.experiments.v3_inter_verticle_communication.g2_eventBus.g2_separateJvms_clustered.publish;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.LocalTime;

public class PublisherApp extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(PublisherApp.class.getName());
    public static final String ADDRESS = "com.percyvega.vertx.eventbus";
    private int countMessages = 0;

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + PublisherApp.class.getSimpleName());

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true);

        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if(resultHandler.succeeded()) {
                Vertx vertx = resultHandler.result();
                vertx.deployVerticle(new PublisherApp());
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

        vertx.eventBus().publish(ADDRESS, messageToSend);
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".stop() ***********************************************************");
    }
}
