package com.percyvega.experiments.v3_inter_verticle_communication.g2_eventBus.g1_sameJvm_notClustered.send.consumer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.LocalTime;

import static com.percyvega.experiments.v3_inter_verticle_communication.g2_eventBus.g1_sameJvm_notClustered.send.consumer.SenderVerticle.ADDRESS;

public class Consumer1Verticle extends AbstractVerticle {
    private static final Logger log = LogManager.getLogger(Consumer1Verticle.class.getName());

    @Override
    public void start() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".start() ***********************************************************");

        vertx.eventBus().consumer(ADDRESS, messageHandler -> {
            JsonObject toReply = new JsonObject()
                    .put("Answer", LocalTime.now().toString())
                    .put("served-by", this.getClass().getSimpleName());
            log.info("Request: " + messageHandler.body() + " - To respond: " + toReply);
            messageHandler.reply(toReply);
        });
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".stop() ***********************************************************");
    }
}
