package com.percyvega.experiments.v1_http.g1;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class V1_Hardcoded_Html extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(V1_Hardcoded_Html.class.getName());

    public static void main(String[] args) {
        log.info("*********************************************************** Running main() from " + V1_Hardcoded_Html.class.getSimpleName());

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new V1_Hardcoded_Html());
    }

    @Override
    public void start() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".start() ***********************************************************");

        vertx
                .createHttpServer()
                .requestHandler(routingContext ->
                    routingContext.response().end(String.format("<h1>Welcome to Vert.x Intro - %s</h1>", this.getClass().getSimpleName()))
                )
                .listen(8080);
    }

    @Override
    public void stop() {
        log.info("*********************************************************** Starting " + this.getClass().getSimpleName() + ".stop() ***********************************************************");
    }
}
