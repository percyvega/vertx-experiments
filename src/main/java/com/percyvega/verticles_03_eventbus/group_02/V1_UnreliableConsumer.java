package com.percyvega.verticles_03_eventbus.group_02;

import com.percyvega.verticles_03_eventbus.group_01.V1_EventBusConsumer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Random;

public class V1_UnreliableConsumer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(V1_EventBusConsumer.class);

    public static void main(String[] args) {
        LOGGER.info("*********************************************************** Hello from " + V1_UnreliableConsumer.class.getSimpleName());

        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true);

        Vertx.clusteredVertx(vertxOptions, resultHandler -> {
            if (resultHandler.succeeded()) {
                Vertx vertx = resultHandler.result();
                vertx.deployVerticle(new V1_UnreliableConsumer());
            }
        });
    }

    @Override
    public void start() {
        LOGGER.info("*********************************************************** Verticle App Started ***********************************************************");

        Random random = new Random();
        vertx.eventBus().<String>consumer("com.percyvega.vertx.eventbus.hello", message -> {
            String messageBody = message.body();
            LOGGER.info("+++++++++++++++++++++++++++++ Received " + messageBody);

            int chaos = random.nextInt(10);
            JsonObject json = new JsonObject().put("served-by", this.toString());

            if (chaos < 6) {
                JsonObject jsonObject;
                if (messageBody.isEmpty()) {
                    jsonObject = json.put("message", "hello");
                } else {
                    jsonObject = json.put("message", "hello " + messageBody);
                }
                LOGGER.info("+++++++++++++++++++++++++++++ Returning " + jsonObject);
                message.reply(jsonObject);
            } else if (chaos < 9) {
                LOGGER.error("+++++++++++++++++++++++++++++ Returning a failure");
                message.fail(500, "message processing failure");
            } else {
                // Just do not reply, leading to a timeout on the consumer side.
                LOGGER.error("+++++++++++++++++++++++++++++ Returning nothing (not replying)");
            }
        });
    }

    @Override
    public void stop() {
        LOGGER.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
