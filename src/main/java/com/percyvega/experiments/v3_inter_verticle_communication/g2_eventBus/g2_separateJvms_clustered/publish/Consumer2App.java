package com.percyvega.experiments.v3_inter_verticle_communication.g2_eventBus.g2_separateJvms_clustered.publish;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.LocalTime;

public class Consumer2App extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(Consumer2App.class.getName());

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + Consumer2App.class.getSimpleName());

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true);

        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if (resultHandler.succeeded()) {
                Vertx vertx = resultHandler.result();
                vertx.deployVerticle(new Consumer2App());
            }
        });
    }

    @Override
    public void start() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".start() ***********************************************************");

        vertx.eventBus().consumer(PublisherApp.ADDRESS, messageHandler -> {
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
