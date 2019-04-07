package com.percyvega.v3_inter_verticle_communication.g4_When_Errors_Happen;

import com.percyvega.v3_inter_verticle_communication.g3_SeparateJvms_EventBus_Clustered.ConsumerApp;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Random;

public class ConsumerUnreliableApp extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(ConsumerApp.class);

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + ConsumerUnreliableApp.class.getSimpleName());

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true);

        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if (resultHandler.succeeded()) {
                Vertx vertx = resultHandler.result();
                vertx.deployVerticle(new ConsumerUnreliableApp());
            }
        });
    }

    @Override
    public void start() {
        log.info("*********************************************************** Verticle App Started ***********************************************************");

        Random random = new Random();
        vertx.eventBus().<String>consumer("com.percyvega.vertx.eventbus.hello", message -> {
            String messageBody = message.body();
            log.info("+++++++++++++++++++++++++++++ Received " + messageBody);

            int chaos = random.nextInt(10);
            JsonObject json = new JsonObject().put("served-by", this.toString());

            if (chaos < 6) {
                JsonObject jsonObject;
                if (messageBody.isEmpty()) {
                    jsonObject = json.put("message", "hello");
                } else {
                    jsonObject = json.put("message", "hello " + messageBody);
                }
                log.info("+++++++++++++++++++++++++++++ Returning " + jsonObject);
                message.reply(jsonObject);
            } else if (chaos < 9) {
                log.error("+++++++++++++++++++++++++++++ Returning a failure");
                message.fail(500, "message processing failure");
            } else {
                // Just do not reply, leading to a timeout on the consumer side.
                log.error("+++++++++++++++++++++++++++++ Returning nothing (not replying)");
            }
        });
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
