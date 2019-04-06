package com.percyvega.verticles_01_http.group_01;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class V1_Hardcoded_Html extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(V1_Hardcoded_Html.class);

    public static void main(String[] args) {
        log.info("*********************************************************** Hello from " + V1_Hardcoded_Html.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new V1_Hardcoded_Html());
    }

    @Override
    public void start() {
        log.info("*********************************************************** Verticle App Started ***********************************************************");

        vertx
                .createHttpServer()
                .requestHandler(routingContext ->
                    routingContext.response().end(String.format("<h1>Welcome to Vert.x Intro - %s</h1>", this.getClass().getSimpleName()))
                )
                .listen(8080);
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Verticle App Stopped ***********************************************************");
    }
}
